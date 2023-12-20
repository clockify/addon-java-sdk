package com.cake.clockify.addonsdk.clockify;

import com.cake.clockify.addonsdk.clockify.model.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyManifest;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifyWebhook;
import com.cake.clockify.addonsdk.shared.Addon;
import lombok.NonNull;

public class ClockifyAddon extends Addon<ClockifyWebhook, ClockifyComponent, ClockifyLifecycleEvent, ClockifySettings> {
    public ClockifyAddon(@NonNull ClockifyManifest manifest) {
        super(manifest);
    }
}
