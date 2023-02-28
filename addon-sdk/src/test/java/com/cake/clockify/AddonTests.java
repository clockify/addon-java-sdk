package com.cake.clockify;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.ClockifyWebhook;
import com.cake.clockify.addonsdk.shared.AddonDescriptor;
import com.cake.clockify.addonsdk.shared.middleware.Middleware;
import com.cake.clockify.addonsdk.shared.middleware.MiddlewareChain;
import com.cake.clockify.addonsdk.shared.model.Manifest;
import com.cake.clockify.addonsdk.shared.request.HttpRequest;
import com.cake.clockify.addonsdk.shared.response.HttpResponse;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddonTests {

    @Test
    public void testAddonHandlers() {
        AddonDescriptor descriptor = Utils.getSampleDescriptor();
        ClockifyAddon clockifyAddon = new ClockifyAddon(descriptor);

        ClockifyComponent component = Utils.getSampleComponent();
        clockifyAddon.registerComponent(component, (r) -> HttpResponse.builder()
                .body("component")
                .build());

        ClockifyWebhook webhook = Utils.getSampleWebhook();
        clockifyAddon.registerWebhook(webhook, (r) -> HttpResponse.builder()
                .body("webhook")
                .build());

        ClockifyLifecycleEvent lifecycleEventInstalled = Utils.getSampleInstalledEvent();
        clockifyAddon.registerLifecycleEvent(lifecycleEventInstalled, (r) -> HttpResponse.builder()
                .body("lifecycle")
                .build());


        HttpResponse componentResponse = clockifyAddon.handle(HttpRequest.builder()
                .method("GET")
                .uri(descriptor.baseUrl() + component.getPath())
                .build());
        assertEquals("component", componentResponse.getBody());

        HttpResponse webhookResponse = clockifyAddon.handle(HttpRequest.builder()
                .method("POST")
                .uri(descriptor.baseUrl() + webhook.getPath())
                .build());
        assertEquals("webhook", webhookResponse.getBody());

        HttpResponse lifecycleResponse = clockifyAddon.handle(HttpRequest.builder()
                .method("POST")
                .uri(descriptor.baseUrl() + lifecycleEventInstalled.getPath())
                .build());
        assertEquals("lifecycle", lifecycleResponse.getBody());

        HttpResponse manifestResponse = clockifyAddon.handle(HttpRequest.builder()
                .method("GET")
                .uri(descriptor.baseUrl() + "/manifest")
                .build());
        Manifest manifest = new Gson().fromJson(manifestResponse.getBody(), Manifest.class);
        assertEquals(descriptor.key(), manifest.getKey());
    }

    @Test
    public void testMiddlewareUsage() {
        AddonDescriptor descriptor = Utils.getSampleDescriptor();
        ClockifyAddon clockifyAddon = new ClockifyAddon(descriptor);

        ClockifyComponent component = Utils.getSampleComponent();
        clockifyAddon.registerComponent(component, (r) -> HttpResponse.builder().build());

        SampleMiddleware middleware1 = new SampleMiddleware();
        SampleMiddleware middleware2 = new SampleMiddleware() {
            @Override
            public HttpResponse apply(HttpRequest request, MiddlewareChain chain) {
                used = true;
                return HttpResponse.builder().body("intercepted").build();
            }
        };
        SampleMiddleware middleware3 = new SampleMiddleware();

        clockifyAddon.useMiddleware(middleware1);
        clockifyAddon.useMiddleware(middleware2);
        clockifyAddon.useMiddleware(middleware3);

        HttpResponse response = clockifyAddon.handle(HttpRequest.builder()
                .method("GET")
                .uri(descriptor.baseUrl() + component.getPath())
                .build());

        assertTrue(middleware1.used);
        assertTrue(middleware2.used);
        assertFalse(middleware3.used);

        assertEquals("intercepted", response.getBody());
    }

    private static class SampleMiddleware implements Middleware {
        boolean used = false;
        @Override
        public HttpResponse apply(HttpRequest request, MiddlewareChain chain) {
            used = true;
            return chain.apply(request);
        }
    }
}
