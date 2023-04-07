package com.cake.clockify.addonsdk.clockify;

import com.cake.clockify.addonsdk.clockify.model.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifyWebhook;
import com.cake.clockify.addonsdk.shared.Addon;
import com.cake.clockify.addonsdk.shared.AddonDescriptor;
import com.cake.clockify.addonsdk.shared.client.HttpClient;
import lombok.NonNull;

public class ClockifyAddon extends Addon<ClockifyWebhook, ClockifyComponent, ClockifyLifecycleEvent, ClockifySettings> {
    public ClockifyAddon(@NonNull AddonDescriptor descriptor) {
        super(descriptor);
    }

    @Override
    public HttpClient getClient() {
        throw new UnsupportedOperationException();
    }
}
