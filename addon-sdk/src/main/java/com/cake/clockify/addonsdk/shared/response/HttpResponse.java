package com.cake.clockify.addonsdk.shared.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class HttpResponse {
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String DEFAULT_CONTENT_TYPE = "application/json";

    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_METHOD_NOT_ALLOWED = 405;
    public static final int STATUS_INTERNAL_SERVER_ERROR = 500;

    @Builder.Default
    private int statusCode = 200;
    @Builder.Default
    private String contentType = DEFAULT_CONTENT_TYPE;
    @Builder.Default
    private Map<String, String> headers = new HashMap<>(0);
    private String body;

    public static class HttpResponseBuilder {
        public HttpResponseBuilder success() {
            return statusCode(STATUS_SUCCESS);
        }

        public HttpResponseBuilder methodNotAllowed() {
            return statusCode(STATUS_METHOD_NOT_ALLOWED);
        }

        public HttpResponseBuilder internalServerError() {
            return statusCode(STATUS_INTERNAL_SERVER_ERROR);
        }
    }
}
