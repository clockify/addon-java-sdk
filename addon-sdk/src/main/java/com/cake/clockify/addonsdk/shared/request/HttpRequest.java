package com.cake.clockify.addonsdk.shared.request;

import lombok.Builder;
import lombok.Getter;

import java.io.Reader;
import java.util.Map;
import java.util.TreeMap;

@Getter
@Builder
public class HttpRequest {
    public static final String HEADER_CLOCKIFY_SIGNATURE = "Clockify-Signature";
    public static final String QUERY_CLOCKIFY_AUTH_TOKEN = "auth_token";

    public static final String POST = "POST";
    public static final String GET = "GET";

    private String method;
    private String uri;
    private Map<String, String[]> query;
    private Map<String, String> headers;
    private Reader bodyReader;

    public HttpRequest(String method, String uri, Map<String, String[]> query,
                       Map<String, String> headers, Reader bodyReader) {
        this.method = method;
        this.uri = uri;

        this.query = new TreeMap<>();
        if (query != null) {
            this.query.putAll(query);
        }

        this.headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        if (headers != null) {
            this.headers.putAll(headers);
        }

        this.bodyReader = bodyReader;
    }
}
