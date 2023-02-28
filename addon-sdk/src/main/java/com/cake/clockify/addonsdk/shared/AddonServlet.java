package com.cake.clockify.addonsdk.shared;

import com.cake.clockify.addonsdk.shared.request.HttpRequest;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

public class AddonServlet extends HttpServlet {

    private final Addon<?, ?, ?, ?> addon;

    public AddonServlet(Addon<?, ?, ?, ?> addon) {
        this.addon = addon;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpResponse response = addon.handle(HttpRequest.builder()
                .method(req.getMethod())
                .uri(req.getRequestURI())
                .headers(getRequestHeaders(req))
                .bodyReader(req.getReader())
                .query(new TreeMap<>(req.getParameterMap()))
                .build());

        writeResponse(response, resp);
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest req) {
        Map<String, String> headers = new TreeMap<>();
        Enumeration<String> headerNames = req.getHeaderNames();

        String header;
        while (headerNames.hasMoreElements()) {
            header = headerNames.nextElement();
            headers.put(header, req.getHeader(header));
        }

        return headers;
    }

    private void writeResponse(HttpResponse response, HttpServletResponse resp) throws IOException {
        response.getHeaders().forEach(resp::setHeader);
        resp.setContentType(response.getContentType());

        if (response.getBody() != null) {
            resp.getWriter().write(response.getBody());
        }
        resp.setStatus(response.getStatusCode());
        resp.flushBuffer();
    }
}
