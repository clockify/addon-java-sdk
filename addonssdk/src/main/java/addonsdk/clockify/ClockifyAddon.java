package addonsdk.clockify;

import addonsdk.clockify.model.ClockifyComponent;
import addonsdk.clockify.model.ClockifyLifecycleEvent;
import addonsdk.clockify.model.ClockifySettings;
import addonsdk.clockify.model.ClockifyWebhook;
import addonsdk.shared.Addon;
import addonsdk.shared.AddonDescriptor;
import addonsdk.shared.client.HttpClient;
import lombok.NonNull;

public class ClockifyAddon extends Addon<ClockifyWebhook, ClockifyComponent, ClockifyLifecycleEvent, ClockifySettings> {
    public ClockifyAddon(@NonNull AddonDescriptor descriptor) {
        super(descriptor);
    }

    @Override
    public HttpClient getClient() {
        return new ClockifyClient();
    }
}
