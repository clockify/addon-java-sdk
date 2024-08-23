package com.cake.clockify;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyManifest;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyWebhook;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ManifestTests {

    @Test
    public void testManifestBuild() {
        ClockifyManifest manifest = Utils.getSampleManifest();
        ClockifyAddon clockifyAddon = new ClockifyAddon(manifest);

        ClockifyComponent component = Utils.getSampleComponent();
        clockifyAddon.registerComponent(component, (r, s) -> {
        });

        ClockifyWebhook webhook = Utils.getSampleWebhook();
        clockifyAddon.registerWebhook(webhook, (r, s) -> {
        });

        ClockifyLifecycleEvent lifecycleEventInstalled = Utils.getSampleInstalledEvent();
        clockifyAddon.registerLifecycleEvent(lifecycleEventInstalled, (r, s) -> {
        });

        ClockifyLifecycleEvent lifecycleEventDeleted = Utils.getSampleDeletedEvent();
        clockifyAddon.registerLifecycleEvent(lifecycleEventDeleted, (r, s) -> {
        });

        assertEquals(1, manifest.getComponents().size());
        assertEquals(component, manifest.getComponents().get(0));

        assertEquals(1, manifest.getWebhooks().size());
        assertEquals(webhook, manifest.getWebhooks().get(0));

        assertEquals(2, manifest.getLifecycle().size());
        assertEquals(lifecycleEventInstalled, manifest.getLifecycle().get(0));
        assertEquals(lifecycleEventDeleted, manifest.getLifecycle().get(1));
    }
}
