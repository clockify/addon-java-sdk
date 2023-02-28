package addonsdk.clockify.model;

import addonsdk.shared.model.SettingHeader;
import annotationprocessor.clockify.ExtendClockifyManifest;
import lombok.Builder;
import lombok.NonNull;

@ExtendClockifyManifest(definition = "settingsHeader")
public class ClockifySettingsHeader extends SettingHeader {

    @Builder
    protected ClockifySettingsHeader(@NonNull String title) {
        super(title);
    }

    public static ClockifySettingsHeaderBuilderTitleStep builder() {
        return new ClockifySettingsHeaderBuilder();
    }

    private static class ClockifySettingsHeaderBuilder implements
            ClockifySettingsHeaderBuilderTitleStep,
            ClockifySettingsHeaderBuilderBuildStep {
    }
}
