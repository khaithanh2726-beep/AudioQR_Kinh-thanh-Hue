import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.io.IOException;
import java.util.concurrent.Semaphore;

// Cầu TCP nội bộ cho công cụ trên Windows; Docker chỉ publish 127.0.0.1:3307.
// Không giữ mật khẩu, không thay xác thực MySQL hoặc truy cập volume dữ liệu.
public class LocalMysqlRelay {
    private static final Semaphore CLIENTS = new Semaphore(16);

    public static void main(String[] args) throws IOException {
        if (args.length != 1 || !args[0].matches("(audioqr-hue|hueqr-rehearsal-[a-f0-9]{8})-database-1")) {
            throw new IllegalArgumentException("Expected the configured database container name");
        }
        try (var listener = new ServerSocket(3306)) {
            while (true) {
                var client = listener.accept();
                if (!CLIENTS.tryAcquire()) { client.close(); continue; }
                Thread.startVirtualThread(() -> relay(client, args[0]));
            }
        }
    }

    private static void relay(Socket client, String host) {
        try (client; var target = new Socket()) {
            target.connect(new InetSocketAddress(host, 3306), 5000);
            client.setSoTimeout(300000);
            target.setSoTimeout(300000);
            var upload = Thread.startVirtualThread(() -> pipe(client, target));
            pipe(target, client);
            upload.join();
        } catch (IOException error) {
            // Không ghi payload SQL, tên khách hoặc thông tin xác thực vào log.
        } catch (InterruptedException error) {
            Thread.currentThread().interrupt();
        } finally {
            CLIENTS.release();
        }
    }

    private static void pipe(Socket from, Socket to) {
        try {
            from.getInputStream().transferTo(to.getOutputStream());
            to.shutdownOutput();
        } catch (IOException error) {
            try { from.close(); } catch (IOException ignored) { }
            try { to.close(); } catch (IOException ignored) { }
        }
    }
}
