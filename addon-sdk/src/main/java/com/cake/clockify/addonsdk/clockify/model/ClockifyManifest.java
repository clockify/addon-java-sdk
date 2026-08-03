package com.cake.clockify.addonsdk.clockify.model;

import com.cake.clockify.annotationprocessor.clockify.ExtendClockifyManifest;

import java.util.List;

@ExtendClockifyManifest
public interface ClockifyManifest {

    String getSchemaVersion();
    String getKey();

    List getLifecycle();

    List getWebhooks();

    List getComponents();

    void setSettings(Object settings);

    static com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyManifestBuilderKeyStep v1_2Builder() {
        return com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyManifest.builder();
    }

    static com.cake.clockify.addonsdk.clockify.model.v1_3.ClockifyManifestBuilderKeyStep v1_3Builder() {
        return com.cake.clockify.addonsdk.clockify.model.v1_3.ClockifyManifest.builder();
    }

    static com.cake.clockify.addonsdk.clockify.model.v1_4.ClockifyManifestBuilderKeyStep v1_4Builder() {
        return com.cake.clockify.addonsdk.clockify.model.v1_4.ClockifyManifest.builder();
    }

    static com.cake.clockify.addonsdk.clockify.model.v1_5.ClockifyManifestBuilderKeyStep v1_5Builder() {
        return com.cake.clockify.addonsdk.clockify.model.v1_5.ClockifyManifest.builder();
    }

    static com.cake.clockify.addonsdk.clockify.model.v1_5_1.ClockifyManifestBuilderKeyStep v1_5_1Builder() {
        return com.cake.clockify.addonsdk.clockify.model.v1_5_1.ClockifyManifest.builder();
    }

    static com.cake.clockify.addonsdk.clockify.model.v1_6.ClockifyManifestBuilderKeyStep v1_6Builder() {
        return com.cake.clockify.addonsdk.clockify.model.v1_6.ClockifyManifest.builder();
    }
}
