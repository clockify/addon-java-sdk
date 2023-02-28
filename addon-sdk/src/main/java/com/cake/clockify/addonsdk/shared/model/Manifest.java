package com.cake.clockify.addonsdk.shared.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor
public class Manifest {

    @NonNull
    @Builder.Default
    private String schemaVersion = "1.0";

    @NonNull
    private String key;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private String baseUrl;
    @NonNull
    @Builder.Default
    private List<LifecycleEvent> lifecycle = new ArrayList<>();
    @NonNull
    @Builder.Default
    private List<Webhook> webhooks = new ArrayList<>();
    @NonNull
    @Builder.Default
    private List<Component> components = new ArrayList<>();
    private Settings settings;

    public void addLifecycleEvent(LifecycleEvent lifecycleEvent) {
        lifecycle.add(lifecycleEvent);
    }

    public void addWebhook(Webhook webhook) {
        webhooks.add(webhook);
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public List<LifecycleEvent> getLifecycle() {
        return List.copyOf(lifecycle);
    }

    public List<Webhook> getWebhooks() {
        return List.copyOf(webhooks);
    }

    public List<Component> getComponents() {
        return List.copyOf(components);
    }

    public Settings getSettings() {
        return settings;
    }
}
