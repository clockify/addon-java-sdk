package com.cake.clockify.addonsdk.shared;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AddonServlet extends HttpServlet {

    private final Addon<?> addon;

    public AddonServlet(Addon<?> addon) {
        this.addon = addon;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        addon.handle(req, resp);
        resp.flushBuffer();
    }
}
