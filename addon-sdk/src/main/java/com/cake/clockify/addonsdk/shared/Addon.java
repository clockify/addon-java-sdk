package com.cake.clockify.addonsdk.shared;

import com.cake.clockify.addonsdk.shared.client.HttpClient;
import com.cake.clockify.addonsdk.shared.middleware.Middleware;
import com.cake.clockify.addonsdk.shared.middleware.MiddlewareChain;
import com.cake.clockify.addonsdk.shared.model.Component;
import com.cake.clockify.addonsdk.shared.model.LifecycleEvent;
import com.cake.clockify.addonsdk.shared.model.Manifest;
import com.cake.clockify.addonsdk.shared.model.Settings;
import com.cake.clockify.addonsdk.shared.model.Webhook;
import com.cake.clockify.addonsdk.shared.request.HttpRequest;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;
import com.cake.clockify.addonsdk.shared.utils.Utils;
import com.cake.clockify.addonsdk.shared.utils.ValidationUtils;
import lombok.Getter;
import lombok.NonNull;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public abstract class Addon<W extends Webhook, C extends Component, L extends LifecycleEvent, S extends Settings> implements RequestHandler {
    public static final String PATH_MANIFEST = "/manifest";

    private static final String ERROR_PATH_ALREADY_REGISTERED = "Handler has already been registered.";

    @Getter
    protected final Manifest manifest;

    private final Map<Request, RequestHandler<?>> requestHandlers = new HashMap<>();
    private final List<Middleware> middlewares = new LinkedList<>();

    protected Addon(@NonNull AddonDescriptor descriptor) {
        this(descriptor, PATH_MANIFEST);
    }

    protected Addon(@NonNull AddonDescriptor descriptor, String manifestPath) {
        if (!ValidationUtils.isValidBaseUrl(descriptor.baseUrl())) {
            throw new ValidationException("The supplied baseUrl is not valid");
        }

        this.manifest = Manifest.builder()
                .key(descriptor.key())
                .name(descriptor.name())
                .description(descriptor.description())
                .baseUrl(descriptor.baseUrl())
                .iconPath(descriptor.iconPath())
                .build();

        this.requestHandlers.put(new Request(manifestPath, HttpRequest.GET),
                request -> HttpResponse.builder()
                        .success()
                        .body(Utils.GSON.toJson(manifest))
                        .build());
    }

    public abstract HttpClient getClient();

    @Override
    public HttpResponse handle(HttpRequest request) {
        try {
            URI uri = URI.create(request.getUri());
            String path = Utils.trimTrailingSlash(uri.getPath());
            String method = request.getMethod();

            RequestHandler<?> handler = requestHandlers.get(new Request(path, method));
            if (handler == null) {
                return HttpResponse.builder()
                        .methodNotAllowed()
                        .build();
            }

            RequestExecutor executor = new RequestExecutor(handler, middlewares);
            return executor.apply(request);
        } catch (Exception e) {
            e.printStackTrace();

            return HttpResponse.builder()
                    .internalServerError()
                    .build();
        }
    }

    public synchronized void registerHandler(String path, String method, RequestHandler<?> handler) {
        if (!ValidationUtils.isValidManifestPath(path)) {
            throw new ValidationException("Url should be an absolute path and not end with a slash.");
        }

        Request key = new Request(path, method);

        if (requestHandlers.containsKey(key)) {
            throw new IllegalArgumentException(ERROR_PATH_ALREADY_REGISTERED);
        }

        requestHandlers.put(key, handler);
    }

    public void registerWebhook(W webhook, RequestHandler<?> handler) {
        registerHandler(webhook.getPath(), HttpRequest.POST, handler);
        manifest.addWebhook(webhook);
    }

    public void registerLifecycleEvent(L lifecycleEvent, RequestHandler<?> handler) {
        registerHandler(lifecycleEvent.getPath(), HttpRequest.POST, handler);
        manifest.addLifecycleEvent(lifecycleEvent);
    }

    public void registerComponent(C component, RequestHandler<?> handler) {
        registerHandler(component.getPath(), HttpRequest.GET, handler);
        manifest.addComponent(component);
    }

    public void registerSettings(S settings) {
        manifest.setSettings(settings);
    }

    public List<Request> getRegisteredRequests() {
        return requestHandlers.keySet().stream().toList();
    }

    public synchronized void useMiddleware(Middleware middleware) {
        middlewares.add(middleware);
    }

    public record Request(String path, String method) {
    }

    private static class RequestExecutor implements MiddlewareChain {
        private final LinkedList<Middleware> middlewares;
        private final RequestHandler handler;

        public RequestExecutor(RequestHandler<?> handler, List<Middleware> middlewares) {
            this.middlewares = new LinkedList<>(middlewares);
            this.handler = handler;
        }

        @Override
        public HttpResponse apply(HttpRequest request) {
            if (middlewares.isEmpty()) {
                return handler.handle(request);
            }
            return middlewares.pop().apply(request, this);
        }
    }
}
