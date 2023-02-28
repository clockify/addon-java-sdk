package com.cake.clockify;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifyWebhook;
import com.cake.clockify.addonsdk.shared.AddonDescriptor;
import com.cake.clockify.addonsdk.shared.model.Manifest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ManifestTests {

    @Test
    public void testManifestBuild() {
        AddonDescriptor descriptor = Utils.getSampleDescriptor();
        ClockifyAddon clockifyAddon = new ClockifyAddon(descriptor);

        ClockifyComponent component = Utils.getSampleComponent();
        clockifyAddon.registerComponent(component, (r) -> null);

        ClockifyWebhook webhook = Utils.getSampleWebhook();
        clockifyAddon.registerWebhook(webhook, (r) -> null);

        ClockifyLifecycleEvent lifecycleEventInstalled = Utils.getSampleInstalledEvent();
        clockifyAddon.registerLifecycleEvent(lifecycleEventInstalled, (r) -> null);

        ClockifyLifecycleEvent lifecycleEventDeleted = Utils.getSampleDeletedEvent();
        clockifyAddon.registerLifecycleEvent(lifecycleEventDeleted, (r) -> null);

        ClockifySettings setting1 = Utils.getSampleSettings();
        clockifyAddon.registerSettings(setting1);

        Manifest manifest = clockifyAddon.getManifest();

        assertEquals(descriptor.key(), manifest.getKey());
        assertEquals(descriptor.name(), manifest.getName());
        assertEquals(descriptor.description(), manifest.getDescription());
        assertEquals(descriptor.baseUrl(), manifest.getBaseUrl());

        assertEquals(1, manifest.getComponents().size());
        assertEquals(component, manifest.getComponents().get(0));

        assertEquals(1, manifest.getWebhooks().size());
        assertEquals(webhook, manifest.getWebhooks().get(0));

        assertEquals(2, manifest.getLifecycle().size());
        assertEquals(lifecycleEventInstalled, manifest.getLifecycle().get(0));
        assertEquals(lifecycleEventDeleted, manifest.getLifecycle().get(1));

        assertNotNull(manifest.getSettings());
        assertEquals(setting1, manifest.getSettings());
    }
}
