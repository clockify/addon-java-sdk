package addonsdk.shared.middleware;

import addonsdk.shared.request.HttpRequest;
import addonsdk.shared.response.HttpResponse;

public interface Middleware {
    HttpResponse apply(HttpRequest request, MiddlewareChain chain);
}
