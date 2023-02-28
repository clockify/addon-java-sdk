package addonsdk.shared.response;

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

    @Builder.Default
    private int statusCode = 200;
    @Builder.Default
    private String contentType = DEFAULT_CONTENT_TYPE;
    @Builder.Default
    private Map<String, String> headers = new HashMap<>(0);
    private String body;
}
