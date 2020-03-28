package ba.unsa.etf.si;

import javafx.scene.image.Image;

public interface IKonverzija {
    public Image base64ToImageDecoder (String base64input);
    public String imageToBase64Encoder (Image image);
}
