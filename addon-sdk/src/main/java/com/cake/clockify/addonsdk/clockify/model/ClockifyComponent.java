package com.cake.clockify.addonsdk.clockify.model;

import com.cake.clockify.addonsdk.shared.model.Component;
import com.cake.clockify.annotationprocessor.clockify.ExtendClockifyManifest;
import lombok.Builder;
import lombok.NonNull;

import java.util.Map;

@ExtendClockifyManifest(definition = "component")
public class ClockifyComponent extends Component {

    @Builder
    protected ClockifyComponent(@NonNull String type, @NonNull String accessLevel,
                             @NonNull String path,
                             String label, String iconPath, Map<String, Object> options) {
        super(type, accessLevel, path, label, iconPath, options);
    }

    public static ClockifyComponentBuilderTypeStep builder() {
        return new ClockifyComponentBuilder();
    }

    private static class ClockifyComponentBuilder implements
            ClockifyComponentBuilderPathStep,
            ClockifyComponentBuilderTypeStep,
            ClockifyComponentBuilderAccessLevelStep,
            ClockifyComponentBuilderOptionalStep {
    }
}
