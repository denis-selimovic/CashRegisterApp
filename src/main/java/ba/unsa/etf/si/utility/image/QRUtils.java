package ba.unsa.etf.si.utility.image;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import java.awt.image.BufferedImage;

public class QRUtils {
    private QRUtils() {}

    private static BufferedImage generateQRCode(String code, int width, int height) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
    //test 2
    public static Image getQRImage(String code, int width, int height) {
        try {
            return SwingFXUtils.toFXImage(generateQRCode(code, width, height), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}