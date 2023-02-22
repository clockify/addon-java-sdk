import addonsdk.clockify.model.ClockifyComponent;
import addonsdk.clockify.model.ClockifyLifecycleEvent;
import addonsdk.clockify.model.ClockifySetting;
import addonsdk.clockify.model.ClockifySettings;
import addonsdk.clockify.model.ClockifySettingsHeader;
import addonsdk.clockify.model.ClockifySettingsTab;
import addonsdk.clockify.model.ClockifyWebhook;
import addonsdk.shared.AddonDescriptor;
import addonsdk.shared.model.SettingsTab;
import addonsdk.shared.model.Setting;

import java.util.List;
import java.util.Map;

public class Utils {

    public static AddonDescriptor getSampleDescriptor() {
        return new AddonDescriptor(
                "key",
                "name",
                "description",
                "http://localhost:8080"
        );
    }

    public static ClockifyComponent getSampleComponent() {
        return ClockifyComponent
                .builder()
                .widget()
                .allowAdmins()
                .path("/component1")
                .label("label")
                .options(Map.of())
                .build();
    }

    public static ClockifyWebhook getSampleWebhook() {
        return ClockifyWebhook.builder()
                .onBalanceUpdated()
                .path("/webhook1")
                .build();
    }

    public static ClockifyLifecycleEvent getSampleInstalledEvent() {
        return ClockifyLifecycleEvent.builder()
                .path("/installed")
                .onInstalled()
                .build();
    }

    public static ClockifyLifecycleEvent getSampleDeletedEvent() {
        return ClockifyLifecycleEvent.builder()
                .path("/deleted")
                .onDeleted()
                .build();
    }

    public static ClockifySettings getSampleSettings() {
        return ClockifySettings.builder()
                .settingsTabs(List.of(
                        ClockifySettingsTab.builder()
                                .id("id")
                                .name("name")
                                .header(ClockifySettingsHeader.builder().title("title").build())
                                .settings(List.of(
                                        ClockifySetting.builder()
                                                .id("id")
                                                .name("name")
                                                .asNumber()
                                                .value(12)
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }
}
