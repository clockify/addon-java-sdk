package addonsdk.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
public class Setting {
    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private String type;

    private Object value;

    private String key;
    private String description;
    private String placeholder;

    private List<String> allowedValues;

    private Boolean required;
    private Boolean copyable;
    private Boolean readOnly;
}
