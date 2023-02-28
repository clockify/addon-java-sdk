package com.cake.clockify.addonsdk.clockify.model;

import com.cake.clockify.addonsdk.shared.model.Webhook;
import com.cake.clockify.annotationprocessor.clockify.ExtendClockifyManifest;
import lombok.Builder;
import lombok.NonNull;

@ExtendClockifyManifest(definition = "webhook")
public class ClockifyWebhook extends Webhook {
    @Builder
    protected ClockifyWebhook(@NonNull String event,
                            @NonNull String path) {
        super(event, path);
    }

    public static ClockifyWebhookBuilderEventStep builder() {
        return new ClockifyWebhookBuilder();
    }

    private static class ClockifyWebhookBuilder implements
            ClockifyWebhookBuilderEventStep,
            ClockifyWebhookBuilderPathStep,
            ClockifyWebhookBuilderBuildStep {
    }
}
