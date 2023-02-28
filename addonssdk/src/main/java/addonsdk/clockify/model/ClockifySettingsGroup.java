package addonsdk.clockify.model;

import addonsdk.shared.model.SettingsGroup;
import annotationprocessor.clockify.ExtendClockifyManifest;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@ExtendClockifyManifest(definition = "settingsGroup")
public class ClockifySettingsGroup extends SettingsGroup<ClockifySetting> {

    @Builder
    protected ClockifySettingsGroup(@NonNull String id, @NonNull String title,
                                    ClockifySettingsHeader header, String description,
                                    List<ClockifySetting> settings) {
        super(id, title, header, description, settings);
    }

    public static ClockifySettingsGroupBuilderIdStep builder() {
        return new ClockifySettingsGroupBuilder();
    }

    private static class ClockifySettingsGroupBuilder implements
            ClockifySettingsGroupBuilderIdStep,
            ClockifySettingsGroupBuilderTitleStep,
            ClockifySettingsGroupBuilderOptionalStep {
    }
}
