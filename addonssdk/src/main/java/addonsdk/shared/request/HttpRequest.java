package addonsdk.shared.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class HttpRequest {
    public static final String HEADER_CLOCKIFY_SIGNATURE = "Clockify-Signature";

    public static final String POST = "POST";
    public static final String GET = "GET";

    private String method;
    private String uri;
    @Builder.Default
    private Map<String, String[]> query = new HashMap<>(0);
    @Builder.Default
    private Map<String, String> headers = new HashMap<>(0);
    private Reader bodyReader;
}
