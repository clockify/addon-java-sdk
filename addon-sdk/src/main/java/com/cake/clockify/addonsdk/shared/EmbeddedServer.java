package com.cake.clockify.addonsdk.shared;

import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

@RequiredArgsConstructor
public class EmbeddedServer {
    private final AddonServlet servlet;
    private Server server;
    private boolean started = false;

    public synchronized void start(int port) throws Exception {
        start(port, "/*");
    }

    public synchronized void start(int port, String pathSpec) throws Exception {
        if (started) {
            return;
        }

        server = new Server(port);

        var handler = new ServletContextHandler();
        server.setHandler(handler);
        handler.addServlet(new ServletHolder(servlet), pathSpec);

        server.start();
        server.join();

        started = true;
    }

    public synchronized void stop() throws Exception {
        if (!started) {
            return;
        }

        server.stop();
        started = false;
    }
}
