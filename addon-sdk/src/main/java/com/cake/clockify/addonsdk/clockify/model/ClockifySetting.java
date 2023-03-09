package com.cake.clockify.addonsdk.clockify.model;

import com.cake.clockify.addonsdk.shared.model.Setting;
import com.cake.clockify.annotationprocessor.clockify.ExtendClockifyManifest;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

@ExtendClockifyManifest(definition = "setting")
public class ClockifySetting extends Setting {

    @Builder
    public ClockifySetting(@NonNull String id, @NonNull String name, @NonNull String accessLevel,
                           @NonNull String type, Object value, String key, String description,
                           String placeholder, List<String> allowedValues, Boolean required,
                           Boolean copyable, Boolean readOnly) {
        super(id, name, accessLevel, type, value, key, description, placeholder, allowedValues,
                required, copyable, readOnly);
    }

    public static ClockifySettingBuilderIdStep builder() {
        return new ClockifySettingBuilder();
    }

    private static class ClockifySettingBuilder implements
            ClockifySettingBuilderIdStep,
            ClockifySettingBuilderNameStep,
            ClockifySettingBuilderAccessLevelStep,
            ClockifySettingBuilderTypeStep,
            ClockifySettingBuilderValueStep,
            ClockifySettingBuilderOptionalStep {

    }
}
