package ba.unsa.etf.si.utility;

import ba.unsa.etf.si.App;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.scene.image.Image;
import java.nio.file.FileSystems;

public class QRUtils {

    private static final String PATH = "./src/main/resources/ba/unsa/etf/si/qr/QR.png";
    private static final String FORMAT = "PNG";

    private QRUtils() {}

    private static void generateQRCode(String code, int width, int height) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(code, BarcodeFormat.QR_CODE, width, height);
        MatrixToImageWriter.writeToPath(bitMatrix, FORMAT, FileSystems.getDefault().getPath(PATH));
    }

    public static Image getQRImage(String code, int width, int height) throws Exception {
        generateQRCode(code, width, height);
        return new Image(App.class.getResourceAsStream("qr/QR.png"));
    }
}
