import addonsdk.clockify.ClockifyAddon;
import addonsdk.clockify.model.ClockifyComponent;
import addonsdk.shared.AddonDescriptor;
import addonsdk.shared.AddonServlet;
import addonsdk.shared.EmbeddedServer;
import addonsdk.shared.response.HttpResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import java.net.URL;
import java.net.URLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServletTests {
    private static final int SAMPLE_SERVER_PORT = 10501;

    @Test
    @SneakyThrows
    public void testServerStartup() {
        AddonDescriptor descriptor = Utils.getSampleDescriptor();
        ClockifyAddon clockifyAddon = new ClockifyAddon(descriptor);

        ClockifyComponent component = Utils.getSampleComponent();
        clockifyAddon.registerComponent(component, (r) -> HttpResponse.builder().build());

        AddonServlet servlet = new AddonServlet(clockifyAddon);

        Runnable serverRunnable = () -> {
            try {
                new EmbeddedServer(servlet).start(SAMPLE_SERVER_PORT);
            } catch (Exception e) {
                if (!(e instanceof InterruptedException)) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread serverThread = new Thread(serverRunnable);
        serverThread.start();

        Thread.sleep(3_000);

        URL url = new URL("http://localhost:" + SAMPLE_SERVER_PORT + component.getPath());
        URLConnection connection = url.openConnection();

        assertEquals("application/json", connection.getContentType());

        serverThread.interrupt();
    }
}
