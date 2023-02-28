package addonsdk.clockify.model;

import addonsdk.shared.model.LifecycleEvent;
import annotationprocessor.clockify.ExtendClockifyManifest;
import lombok.Builder;
import lombok.NonNull;

@ExtendClockifyManifest(definition = "lifecycle")
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
