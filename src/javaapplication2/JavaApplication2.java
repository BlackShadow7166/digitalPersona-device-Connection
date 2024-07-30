//package javaapplication2;
//
//import com.digitalpersona.uareu.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.nio.ByteBuffer;
//import java.util.Base64;
//import javax.imageio.ImageIO;
//import com.digitalpersona.uareu.Fid.Fiv;
//
//public class JavaApplication2 {
//    private FingerprintCapture fingerprintCapture;
//
//    public JavaApplication2() {
//        try {
//            this.fingerprintCapture = new FingerprintCapture();
//        } catch (UareUException e) {
//            e.printStackTrace();
//            System.err.println("Error initializing FingerprintCapture: " + e.getMessage());
//        }
//    }
//
//    public void processRequests() {
//        try (BufferedInputStream inputStream = new BufferedInputStream(System.in);
//             BufferedOutputStream outputStream = new BufferedOutputStream(System.out)) {
//
//            byte[] lengthBytes = new byte[4];
//            while (inputStream.read(lengthBytes) == 4) {
//                int length = ByteBuffer.wrap(lengthBytes).order(java.nio.ByteOrder.nativeOrder()).getInt();
//                byte[] messageBytes = new byte[length];
//                int bytesRead = 0;
//                while (bytesRead < length) {
//                    int read = inputStream.read(messageBytes, bytesRead, length - bytesRead);
//                    if (read == -1) {
//                        throw new IOException("Unexpected end of stream.");
//                    }
//                    bytesRead += read;
//                }
//
//                String input = new String(messageBytes, "UTF-8");
//                System.out.println("Received input: " + input);
//
//                if (!input.contains("image_request")) {
//                    BufferedImage image = fingerprintCapture.captureFingerprint();
//                    String response;
//                    if (image != null) {
//                        String base64Image = encodeToBase64(image);
//                        response = "{\"image\":\"" + base64Image + "\"}";
//                    } else {
//                        response = "{\"error\":\"No image captured\"}";
//                    }
//                    byte[] responseBytes = response.getBytes("UTF-8");
//                    writeMessage(outputStream, responseBytes);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error processing requests: " + e.getMessage());
//        }
//    }
//
//    private String encodeToBase64(BufferedImage image) {
//        String base64Image = null;
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//            ImageIO.write(image, "png", baos);
//            byte[] imageBytes = baos.toByteArray();
//            base64Image = Base64.getEncoder().encodeToString(imageBytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error encoding image to base64: " + e.getMessage());
//        }
//        return base64Image;
//    }
//
//    public static void main(String[] args) {
//        JavaApplication2 app = new JavaApplication2();
//        app.processRequests();
//    }
//
//    private void writeMessage(BufferedOutputStream outputStream, byte[] messageBytes) throws IOException {
//        int length = messageBytes.length;
//        byte[] lengthBytes = ByteBuffer.allocate(4).order(java.nio.ByteOrder.nativeOrder()).putInt(length).array();
//        outputStream.write(lengthBytes);
//        outputStream.write(messageBytes);
//        outputStream.flush();
//    }
//
//    public static class FingerprintCapture {
//        private Reader reader;
//
//        public FingerprintCapture() throws UareUException {
//            UareUGlobal.GetReaderCollection();
//        }
//
//        public BufferedImage captureFingerprint() {
//            BufferedImage capturedImage = null;
//            try {
//                ReaderCollection collection = UareUGlobal.GetReaderCollection();
//                collection.GetReaders();
//                if (collection.size() > 0) {
//                    reader = collection.get(0);
//                    reader.Open(Reader.Priority.COOPERATIVE);
//
//                    Reader.CaptureResult captureResult = reader.Capture(Fid.Format.ANSI_381_2004, Reader.ImageProcessing.IMG_PROC_DEFAULT, reader.GetCapabilities().resolutions[0], -1);
//                    if (captureResult.quality == Reader.CaptureQuality.GOOD && captureResult.image != null) {
//                        Fiv view = captureResult.image.getViews()[0];
//                        capturedImage = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//                        capturedImage.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
//                    } else {
//                        System.err.println("Capture quality is not good or image is null.");
//                    }
//                } else {
//                    System.err.println("No readers found.");
//                }
//            } catch (UareUException e) {
//                e.printStackTrace();
//                System.err.println("Error during fingerprint capture: " + e.getMessage());
//            } finally {
//                if (reader != null) {
//                    try {
//                        reader.Close();
//                    } catch (UareUException e) {
//                        e.printStackTrace();
//                        System.err.println("Error closing reader: " + e.getMessage());
//                    }
//                }
//            }
//            return capturedImage;
//        }
//    }
//}
package javaapplication2;

//import com.digitalpersona.uareu.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayOutputStream;
//import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
//import java.nio.ByteBuffer;
//import java.util.Base64;
//import javax.imageio.ImageIO;
//import com.digitalpersona.uareu.Fid.Fiv;
//import java.nio.charset.StandardCharsets;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import java.io.InputStream;
//import java.io.InterruptedIOException;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import java.nio.charset.StandardCharsets;
import java.io.IOException;



public class JavaApplication2 {
//    private FingerprintCapture fingerprintCapture;
//
//    public JavaApplication2() {
//        try {
//            this.fingerprintCapture = new FingerprintCapture();
//        } catch (UareUException e) {
//            e.printStackTrace();
//            System.err.println("Error initializing FingerprintCapture: " + e.getMessage());
//        }
//    }

//    


    public static void main(String[] args) {
         String sIncomingMsg = receiveMessage();
     
        
        String sOutgoingMsg = "{\"text\":\"java host\"}";
        
        sendMessage(sOutgoingMsg);
		
		
//        app.processRequests();
    }
      //Convert length from Bytes to int
    public static int getInt(byte[] bytes) 
    {
        return  (bytes[3] << 24) & 0xff000000|
                (bytes[2] << 16)& 0x00ff0000|
                (bytes[1] << 8) & 0x0000ff00|
                (bytes[0] << 0) & 0x000000ff;
    }

    // Read an input from Chrome Extension
    static public String receiveMessage()
    {

        byte[] b = new byte[4];

        try
        {
            System.in.read(b);
            int size = getInt(b);

            byte[] msg = new byte[size];
            System.in.read(msg);

            // make sure to get message as UTF-8 format
            String msgStr = new String(msg, "UTF-8");

            return msgStr;

        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

    }
    
    public static byte[] getBytes(int length) 
    {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) ( length      & 0xFF);
        bytes[1] = (byte) ((length>>8)  & 0xFF);
        bytes[2] = (byte) ((length>>16) & 0xFF);
        bytes[3] = (byte) ((length>>24) & 0xFF);
        return bytes;
    }

    static public void sendMessage(String pMsg)
    {
        try 
        {
            System.out.write(getBytes(pMsg.length()));
            
            byte[] bytes = pMsg.getBytes();
            
            System.out.write(bytes);
        } 
        catch (IOException ex) 
        {
            ex.printStackTrace();
        }
    }
 
}

