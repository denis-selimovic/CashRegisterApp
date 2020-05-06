package ba.unsa.etf.si.utility.image;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import static ba.unsa.etf.si.models.Product.getDefaultImage;

public class Base64Utils {

    private Base64Utils() {}

    public static Image base64ToImageDecoder(String base64input) throws Exception {
        byte[] decodedBytes = null;

        try {
            if (base64input.contains("data:image/jpeg;")) throw new Exception("JPEG file");
            if (base64input.contains(","))
                decodedBytes = Base64.getMimeDecoder().decode(base64input.split(",")[1]);
            else
                decodedBytes = Base64.getMimeDecoder().decode(base64input);
        } catch (Exception e) {
            try {
                return getDefaultImage();
            } catch (Exception ec) {
                throw new Exception("Fatal error due to incorrect image type or missing default image.");
            }
        }
        ByteArrayInputStream imageArr = new ByteArrayInputStream(decodedBytes);
        return new Image(imageArr);
    }


    public static String imageToBase64Encoder(Image image) {
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        String base64String = null;
        try {
            ImageIO.write(bImage, "png", b);
            byte[] imageBytes = b.toByteArray();

            Base64.Encoder encoder = Base64.getEncoder();
            base64String = encoder.encodeToString(imageBytes);
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64String;
    }
}
