package com.cake.clockify.addonsdk.shared;

import com.cake.clockify.addonsdk.shared.request.HttpRequest;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;

public interface RequestHandler<R extends HttpRequest> {
    HttpResponse handle(R request);
}
