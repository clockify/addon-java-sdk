package com.cake.clockify;

import com.cake.clockify.addonsdk.clockify.model.ClockifyManifest;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifySetting;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifySettings;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifySettingsHeader;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifySettingsTab;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyWebhook;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

public class Utils {

    public static HttpServletRequest getMockedServletRequest(String method, String path) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getMethod()).thenReturn(method);
        Mockito.when(request.getRequestURI()).thenReturn(path);
        return request;
    }

    public static ClockifyManifest getSampleManifest() {
        return ClockifyManifest.v1_2Builder()
                .key("key")
                .name("name")
                .baseUrl("http://localhost:8080")
                .requireFreePlan()
                .scopes(List.of())
                .description("description")
                .build();
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
                .tabs(List.of(
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
