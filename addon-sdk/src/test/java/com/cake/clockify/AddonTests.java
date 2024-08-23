package com.cake.clockify;

import com.cake.clockify.addonsdk.clockify.ClockifyAddon;
import com.cake.clockify.addonsdk.clockify.model.ClockifyManifest;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyComponent;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyLifecycleEvent;
import com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyWebhook;
import com.google.gson.Gson;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.cake.clockify.Utils.getMockedServletRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddonTests {

    @Test
    @SneakyThrows
    public void testAddonHandlers() {
        ClockifyManifest manifest = Utils.getSampleManifest();
        ClockifyAddon addon = new ClockifyAddon(manifest);

        ClockifyComponent component = Utils.getSampleComponent();
        addon.registerComponent(component, (r, s) -> {
            s.getWriter().write("component");
            s.setStatus(200);
        });

        ClockifyWebhook webhook = Utils.getSampleWebhook();
        addon.registerWebhook(webhook, (r, s) -> {
            s.getWriter().write("webhook");
            s.setStatus(200);
        });

        ClockifyLifecycleEvent lifecycleEventInstalled = Utils.getSampleInstalledEvent();
        addon.registerLifecycleEvent(lifecycleEventInstalled, (r, s) -> {
            s.getWriter().write("lifecycle");
            s.setStatus(200);
        });

        StringWriter writer = new StringWriter();

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        Mockito.doNothing().when(response).setStatus(200);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer, true));

        addon.handle(getMockedServletRequest("GET", component.getPath()), response);

        assertEquals("component", writer.getBuffer().toString());
        writer.getBuffer().delete(0, writer.getBuffer().length());

        addon.handle(getMockedServletRequest("POST", webhook.getPath()), response);

        assertEquals("webhook", writer.getBuffer().toString());
        writer.getBuffer().delete(0, writer.getBuffer().length());

        addon.handle(getMockedServletRequest("POST", lifecycleEventInstalled.getPath()), response);

        assertEquals("lifecycle", writer.getBuffer().toString());
        writer.getBuffer().delete(0, writer.getBuffer().length());

        addon.handle(getMockedServletRequest("GET", "/manifest"), response);

        assertEquals(manifest.getKey(), new Gson().fromJson(writer.getBuffer().toString(), com.cake.clockify.addonsdk.clockify.model.v1_2.ClockifyManifest.class).getKey());
    }

    @Test
    @SneakyThrows
    public void testMiddlewareUsage() {
        ClockifyManifest manifest = Utils.getSampleManifest();
        ClockifyAddon clockifyAddon = new ClockifyAddon(manifest);

        ClockifyComponent component = Utils.getSampleComponent();
        clockifyAddon.registerComponent(component, (r, s) -> {
        });

        SampleMiddleware middleware1 = new SampleMiddleware();
        SampleMiddleware middleware2 = new SampleMiddleware() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {

                used = true;
                response.getWriter().write("intercepted");
            }
        };
        SampleMiddleware middleware3 = new SampleMiddleware();

        clockifyAddon.addFilter(middleware1);
        clockifyAddon.addFilter(middleware2);
        clockifyAddon.addFilter(middleware3);

        StringWriter writer = new StringWriter();

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        Mockito.doNothing().when(response).setStatus(200);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));

        clockifyAddon.handle(getMockedServletRequest("GET", component.getPath()), response);

        assertTrue(middleware1.used);
        assertTrue(middleware2.used);
        assertFalse(middleware3.used);

        assertEquals("intercepted", writer.getBuffer().toString());
    }

    private static class SampleMiddleware implements Filter {
        boolean used = false;

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                throws IOException, ServletException {
            used = true;
            chain.doFilter(request, response);
        }
    }
}
