package com.cake.clockify.addonsdk.shared;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@FunctionalInterface
public interface RequestHandler {
    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
