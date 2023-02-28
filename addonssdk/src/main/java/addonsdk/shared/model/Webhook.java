package addonsdk.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public class Webhook {
    @NonNull
    private String event;
    @NonNull
    private String path;
}
