package addonsdk.shared;

import addonsdk.shared.request.HttpRequest;
import addonsdk.shared.response.HttpResponse;

public interface RequestHandler<R extends HttpRequest> {
    HttpResponse handle(R request);
}
