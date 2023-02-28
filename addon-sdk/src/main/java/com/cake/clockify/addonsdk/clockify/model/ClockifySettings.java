package com.cake.clockify.addonsdk.clockify.model;

import com.cake.clockify.addonsdk.shared.model.Settings;
import com.cake.clockify.addonsdk.shared.model.SettingsTab;
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
