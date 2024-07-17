package javaapplication2;

import com.digitalpersona.uareu.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import com.digitalpersona.uareu.Fid.Fiv;

public class JavaApplication2 {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new FingerprintCapture().showCaptureDialog();
            } catch (UareUException e) {
                JOptionPane.showMessageDialog(null, "Error initializing capture: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static class FingerprintCapture extends JPanel implements ActionListener {
        private static final String ACT_CAPTURE = "capture";
        private static final String ACT_EXIT = "exit";

        private JDialog captureDialog;
        private JTextArea statusTextArea;
        private Reader reader;
        private ImagePanel imagePanel;

        public FingerprintCapture() throws UareUException {
            // Initialize capture library
            UareUGlobal.GetReaderCollection();

            // Create UI elements
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            JLabel statusLabel = new JLabel("Status:");
            add(statusLabel);

            statusTextArea = new JTextArea(5, 20);
            statusTextArea.setEditable(false);
            JScrollPane statusScrollPane = new JScrollPane(statusTextArea);
            add(statusScrollPane);

            imagePanel = new ImagePanel();
            imagePanel.setPreferredSize(new Dimension(380, 380));
            add(imagePanel);

            JButton captureButton = new JButton("Capture Fingerprint");
            captureButton.setActionCommand(ACT_CAPTURE);
            captureButton.addActionListener(this);
            add(captureButton);

            JButton exitButton = new JButton("Exit");
            exitButton.setActionCommand(ACT_EXIT);
            exitButton.addActionListener(this);
            add(exitButton);

            captureDialog = new JDialog((JFrame) null, "Fingerprint Capture", true);
            captureDialog.setContentPane(this);
            captureDialog.pack();
            captureDialog.setLocationRelativeTo(null);
        }

        public void showCaptureDialog() {
            captureDialog.setVisible(true);
        }

        private void captureFingerprint() {
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
                    // Display the image
                    imagePanel.showImage(captureResult.image);
                    statusTextArea.append("Fingerprint captured\n");
                } else {
                    statusTextArea.append("Fingerprint capture failed: " + captureResult.quality + "\n");
                }
            } catch (UareUException e) {
                statusTextArea.append("Error capturing fingerprint: " + e.getMessage() + "\n");
            } finally {
                // Close the reader
                if (reader != null) {
                    try {
                        reader.Close();
                    } catch (UareUException e) {
                        statusTextArea.append("Error closing reader: " + e.getMessage() + "\n");
                    }
                }
            }
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String actionCommand = e.getActionCommand();
            if (ACT_CAPTURE.equals(actionCommand)) {
                captureFingerprint();
            } else if (ACT_EXIT.equals(actionCommand)) {
                captureDialog.dispose();
            }
        }
    }

    public static class ImagePanel extends JPanel {
        private static final long serialVersionUID = 5;
        private BufferedImage m_image;

        public void showImage(Fid image) {
            Fiv view = image.getViews()[0];
            m_image = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            m_image.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
            repaint();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            if (m_image != null) {
                g.drawImage(m_image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
