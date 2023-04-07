package com.cake.clockify;

import com.cake.clockify.addonsdk.clockify.model.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifySetting;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettingsHeader;
import com.cake.clockify.addonsdk.clockify.model.ClockifySettingsTab;
import com.cake.clockify.addonsdk.clockify.model.ClockifyWebhook;
import com.cake.clockify.addonsdk.shared.AddonDescriptor;

import java.util.List;
import java.util.Map;

public class Utils {

    public static AddonDescriptor getSampleDescriptor() {
        return new AddonDescriptor(
                "key",
                "name",
                "description",
                "http://localhost:8080",
                null
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
                                                .allowEveryone()
                                                .asNumber()
                                                .value(12)
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }
}
