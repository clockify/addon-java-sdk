package addonsdk.clockify.model;

import addonsdk.shared.model.Settings;
import addonsdk.shared.model.SettingsTab;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

public class ClockifySettings extends Settings<ClockifySetting, ClockifySettingsGroup> {

    @Builder
    protected ClockifySettings(
            @NonNull List<SettingsTab<ClockifySetting, ClockifySettingsGroup>> settingsTabs) {
        super(settingsTabs);
    }

    public static ClockifySettings settingsTabs(
            List<SettingsTab<ClockifySetting, ClockifySettingsGroup>> settingsTabs) {
        return new ClockifySettings(settingsTabs);
    }
}
