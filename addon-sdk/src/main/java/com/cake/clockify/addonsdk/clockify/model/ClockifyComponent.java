package com.cake.clockify.addonsdk.clockify.model;

import com.cake.clockify.addonsdk.shared.model.Component;
import lombok.Builder;
import lombok.NonNull;

import java.util.Map;

public class ClockifyComponent extends Component {

    @Builder
    public ClockifyComponent(@NonNull String type, @NonNull String accessLevel,
                             @NonNull String path,
                             String label, String iconPath, Integer width, Integer height,
                             Map<String, Object> options) {
        super(type, accessLevel, path, label, iconPath, width, height, options);
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
