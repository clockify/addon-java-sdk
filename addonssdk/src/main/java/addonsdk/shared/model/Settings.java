package addonsdk.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
public class Settings<S extends Setting, G extends SettingsGroup<S>> {
    @NonNull
    private List<SettingsTab<S, G>> tabs;
}
