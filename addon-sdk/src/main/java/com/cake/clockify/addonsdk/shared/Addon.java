package com.cake.clockify.addonsdk.shared;

import com.cake.clockify.addonsdk.shared.utils.Utils;
import com.cake.clockify.addonsdk.shared.utils.ValidationUtils;
import com.google.gson.Gson;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.jetty.http.HttpStatus;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class Addon<M> implements RequestHandler {
    public static final String PATH_MANIFEST = "/manifest";

    private static final String ERROR_PATH_ALREADY_REGISTERED = "Handler has already been registered.";
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";

    @Getter
    protected final M manifest;

    protected final Gson gson;

    private final Map<Request, RequestHandler> requestHandlers = new HashMap<>();
    private final List<Filter> filters = new LinkedList<>();

    protected Addon(@NonNull M manifest) {
        this(manifest, PATH_MANIFEST);
    }

    protected Addon(@NonNull M manifest, String manifestPath) {
        this.gson = new Gson();
        this.manifest = manifest;

        registerHandler(manifestPath, HTTP_GET, (request, response) -> {
            gson.toJson(manifest, response.getWriter());
            response.setStatus(HttpStatus.OK_200);
        });
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = Utils.trimTrailingSlash(request.getRequestURI());
            String method = request.getMethod();

            RequestHandler handler = requestHandlers.get(new Request(path, method));
            if (handler != null) {
                new RequestExecutor(handler, filters).doFilter(request, response);
                return;
            }

            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED_405);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

    public synchronized void registerHandler(String path, String method, RequestHandler handler) {
        if (!ValidationUtils.isValidManifestPath(path)) {
            throw new ValidationException("Url should be an absolute path and not end with a slash.");
        }

        Request key = new Request(path, method);

        if (requestHandlers.containsKey(key)) {
            throw new IllegalArgumentException(ERROR_PATH_ALREADY_REGISTERED);
        }

        requestHandlers.put(key, handler);
    }

    public List<Request> getRegisteredRequests() {
        return requestHandlers.keySet().stream().toList();
    }

    public synchronized void addFilter(Filter filter) {
        filters.add(filter);
    }

    public record Request(String path, String method) {
    }

    private static class RequestExecutor implements FilterChain {
        private final LinkedList<Filter> filters;
        private final RequestHandler handler;

        public RequestExecutor(RequestHandler handler, List<Filter> filters) {
            this.filters = new LinkedList<>(filters);
            this.handler = handler;
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response) throws ServletException, IOException {
            if (filters.isEmpty()) {
                handler.handle((HttpServletRequest) request, (HttpServletResponse) response);
            } else {
                filters.pop().doFilter(request, response, this);
            }
        }
    }
}
