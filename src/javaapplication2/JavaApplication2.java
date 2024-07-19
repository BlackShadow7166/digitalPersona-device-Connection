
package javaapplication2;

import com.digitalpersona.uareu.*;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import com.digitalpersona.uareu.Fid.Fiv;

public class JavaApplication2 extends WebSocketServer {

    private FingerprintCapture fingerprintCapture;

    public JavaApplication2(InetSocketAddress address) {
        super(address);
        try {
            this.fingerprintCapture = new FingerprintCapture();
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch) {
        System.out.println("New connection from " + ws.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket ws, int code, String reason, boolean remote) {
        System.out.println("Closed connection to " + ws.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        System.out.println("Message from client: " + message);
        if ("image_request".equals(message)) {
            try {
                BufferedImage image = fingerprintCapture.captureFingerprint();
                if (image != null) {
                    String base64Image = encodeToBase64(image);
                    System.out.print(base64Image);
                    ws.send(base64Image);
                } else {
                    ws.send("Error capturing image");
                }
            } catch (Exception e) {
                ws.send("Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(WebSocket ws, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully");
    }

    private String encodeToBase64(BufferedImage image) {
        String base64Image = null;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            base64Image = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64Image;
    }

    public static void main(String[] args) {
        int port = 8080; // Define your port
        WebSocketServer server = new JavaApplication2(new InetSocketAddress(port));
        server.start();
        System.out.println("WebSocket server started on port: " + port);
    }

    public static class FingerprintCapture {
        private Reader reader;

        public FingerprintCapture() throws UareUException {
            // Initialize capture library
            UareUGlobal.GetReaderCollection();
        }

        public BufferedImage captureFingerprint() {
            BufferedImage capturedImage = null;
            try {
                // Get the first available reader
                ReaderCollection collection = UareUGlobal.GetReaderCollection();
                collection.GetReaders();
                reader = collection.get(0);

                // Open the reader
                reader.Open(Reader.Priority.COOPERATIVE);

                // Capture the fingerprint
                Reader.CaptureResult captureResult = reader.Capture(Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT, reader.GetCapabilities().resolutions[0], -1);

                // Check capture quality
                if (captureResult.quality == Reader.CaptureQuality.GOOD && captureResult.image != null) {
                    Fiv view = captureResult.image.getViews()[0];
                    capturedImage = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
                    capturedImage.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
                }
            } catch (UareUException e) {
                e.printStackTrace();
            } finally {
                // Close the reader
                if (reader != null) {
                    try {
                        reader.Close();
                    } catch (UareUException e) {
                        e.printStackTrace();
                    }
                }
            }
            return capturedImage;
        }
    }
}
