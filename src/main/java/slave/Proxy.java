package slave;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Proxy {
    private void proxy(InputStream in, OutputStream out) {
        new Thread(()-> {
            try {
//                byte[] reply = new byte[4096];
//                int bytesRead;
//                while (-1 != (bytesRead = in.read(reply))) {
//                    out.write(reply, 0, bytesRead);
//                }

                while (true) {
                    IOUtils.copy(in,out);
                }
            } catch (Exception ex) {
//                ex.printStackTrace();
            }
        }).start();
    }

    public Proxy(int localPort, int remotePort) {
        new Thread(() -> {
            try (ServerSocket listener = new ServerSocket(remotePort); Socket client = new Socket("localhost", localPort)) {
                while (true) {
                    try (Socket socket = listener.accept()) {
                        proxy(socket.getInputStream(), client.getOutputStream());
                        proxy(client.getInputStream(),socket.getOutputStream());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
