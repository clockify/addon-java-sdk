package com.cake.clockify.addonsdk.clockify.model;

import com.cake.clockify.addonsdk.shared.model.LifecycleEvent;
import lombok.Builder;
import lombok.NonNull;

public class ClockifyLifecycleEvent extends LifecycleEvent {

    @Builder
    protected ClockifyLifecycleEvent(@NonNull String type, @NonNull String path) {
        super(type, path);
    }

    public static ClockifyLifecycleEventBuilderPathStep builder() {
        return new ClockifyLifecycleEventBuilder();
    }

    private static class ClockifyLifecycleEventBuilder implements
            ClockifyLifecycleEventBuilderTypeStep,
            ClockifyLifecycleEventBuilderPathStep,
            ClockifyLifecycleEventBuilderBuildStep {
    }
}
