package addonsdk.shared.middleware;

import addonsdk.shared.request.HttpRequest;
import addonsdk.shared.response.HttpResponse;

public interface MiddlewareChain {
    HttpResponse apply(HttpRequest request);
}
