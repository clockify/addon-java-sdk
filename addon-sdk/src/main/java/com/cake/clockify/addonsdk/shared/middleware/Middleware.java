package com.cake.clockify.addonsdk.shared.middleware;

import com.cake.clockify.addonsdk.shared.request.HttpRequest;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;

public interface Middleware {
    HttpResponse apply(HttpRequest request, MiddlewareChain chain);
}
