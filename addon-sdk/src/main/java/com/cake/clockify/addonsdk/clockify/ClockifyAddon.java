package com.cake.clockify.addonsdk.clockify;

import com.cake.clockify.addonsdk.clockify.model.ClockifyManifest;
import com.cake.clockify.addonsdk.clockify.model.ClockifyResource;
import com.cake.clockify.addonsdk.shared.Addon;
import com.cake.clockify.addonsdk.shared.RequestHandler;
import lombok.NonNull;

public class ClockifyAddon extends Addon<ClockifyManifest> {
    public ClockifyAddon(@NonNull ClockifyManifest manifest) {
        super(manifest);
    }

    public void registerWebhook(ClockifyResource webhook, RequestHandler handler) {
        registerHandler(webhook.getPath(), HTTP_POST, handler);
        manifest.getWebhooks().add(webhook);
    }

    public void registerLifecycleEvent(ClockifyResource lifecycleEvent, RequestHandler handler) {
        registerHandler(lifecycleEvent.getPath(), HTTP_POST, handler);
        manifest.getLifecycle().add(lifecycleEvent);
    }

    public void registerComponent(ClockifyResource component, RequestHandler handler) {
        registerHandler(component.getPath(), HTTP_GET, handler);
        manifest.getComponents().add(component);
    }

    public void registerCustomSettings(String path, RequestHandler handler) {
        registerHandler(path, HTTP_GET, handler);
        manifest.setSettings(path);
    }
}
