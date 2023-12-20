package com.cake.clockify.addonsdk.shared.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
@AllArgsConstructor
public class Manifest {

    @NonNull
    protected String schemaVersion;

    @NonNull
    protected String key;
    @NonNull
    protected String name;
    protected String description;
    @NonNull
    protected String baseUrl;
    protected String iconPath;
    @NonNull
    protected String minimalSubscriptionPlan;
    @NonNull
    protected List<LifecycleEvent> lifecycle;
    @NonNull
    protected List<Webhook> webhooks;
    @NonNull
    protected List<Component> components;
    @NonNull
    protected List<String> scopes;

    /**
     * One of: {@link Settings}, {@link String}
     */
    private Object settings;

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

    public void setCustomSettingsPath(String settingsPath) {
        this.settings = settingsPath;
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

    public Object getSettings() {
        return settings;
    }
}
