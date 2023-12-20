package com.cake.clockify.addonsdk.clockify.model;

import com.cake.clockify.addonsdk.shared.model.Manifest;
import com.cake.clockify.annotationprocessor.clockify.ExtendClockifyManifest;
import lombok.Builder;

import java.util.LinkedList;
import java.util.List;

@ExtendClockifyManifest
public class ClockifyManifest extends Manifest {

    @Builder
    protected ClockifyManifest(String key,
                               String name,
                               String description,
                               String baseUrl,
                               String iconPath,
                               String minimalSubscriptionPlan,
                               List<ClockifyLifecycleEvent> lifecycle,
                               List<ClockifyWebhook> webhooks,
                               List<ClockifyComponent> components,
                               List<String> scopes,
                               Object settings) {
        super(
                "1.2",
                key,
                name,
                description,
                baseUrl,
                iconPath,
                minimalSubscriptionPlan,
                lifecycle == null ? new LinkedList<>() : new LinkedList<>(lifecycle),
                webhooks == null ? new LinkedList<>() : new LinkedList<>(webhooks),
                components == null ? new LinkedList<>() : new LinkedList<>(components),
                scopes == null ? new LinkedList<>() : new LinkedList<>(scopes),
                settings
        );
    }

    public static ClockifyManifestBuilderKeyStep builder() {
        return new ClockifyManifestBuilder();
    }

    private static class ClockifyManifestBuilder implements
            ClockifyManifestBuilderKeyStep,
            ClockifyManifestBuilderNameStep,
            ClockifyManifestBuilderBaseUrlStep,
            ClockifyManifestBuilderMinimalSubscriptionPlanStep,
            ClockifyManifestBuilderScopesStep,
            ClockifyManifestBuilderOptionalStep {
    }
}
