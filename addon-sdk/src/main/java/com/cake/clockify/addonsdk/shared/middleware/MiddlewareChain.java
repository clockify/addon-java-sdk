package com.cake.clockify.addonsdk.shared.middleware;

import com.cake.clockify.addonsdk.shared.request.HttpRequest;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;

public interface MiddlewareChain {
    HttpResponse apply(HttpRequest request);
}
