package addonsdk.clockify.model;

import addonsdk.shared.model.SettingsTab;
import annotationprocessor.clockify.ExtendClockifyManifest;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@ExtendClockifyManifest(definition = "settingsTab")
public class ClockifySettingsTab extends SettingsTab<ClockifySetting, ClockifySettingsGroup> {

    @Builder
    protected ClockifySettingsTab(@NonNull String id, @NonNull String name,
                                  ClockifySettingsHeader header,
                                  List<ClockifySetting> settings,
                                  List<ClockifySettingsGroup> groups) {
        super(id, name, header, settings, groups);
    }

    public static ClockifySettingsTabBuilderIdStep builder() {
        return new ClockifySettingsTabBuilder();
    }

    private static class ClockifySettingsTabBuilder implements
            ClockifySettingsTabBuilderIdStep,
            ClockifySettingsTabBuilderNameStep,
            ClockifySettingsTabBuilderOptionalStep {
    }
}
