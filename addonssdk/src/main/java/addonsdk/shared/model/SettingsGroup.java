package addonsdk.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
public class SettingsGroup<S extends Setting> {
    @NonNull
    private String id;
    @NonNull
    private String title;

    private SettingHeader header;
    private String description;
    private List<S> settings;
}
