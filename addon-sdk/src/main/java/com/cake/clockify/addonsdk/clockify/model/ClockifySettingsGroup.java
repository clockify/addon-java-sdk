package com.cake.clockify.addonsdk.clockify.model;

import com.cake.clockify.addonsdk.shared.model.SettingsGroup;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

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
            ClockifySettingsGroupBuilderSettingsStep,
            ClockifySettingsGroupBuilderOptionalStep {
    }
}
