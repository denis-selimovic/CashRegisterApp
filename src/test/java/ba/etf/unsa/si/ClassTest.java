package ba.etf.unsa.si;

import ba.unsa.etf.si.controllers.LoginFormController;
import ba.unsa.etf.si.models.Product;
import ba.unsa.etf.si.models.Receipt;
import ba.unsa.etf.si.models.enums.PaymentMethod;
import ba.unsa.etf.si.utility.date.DateConverter;
import ba.unsa.etf.si.utility.image.Base64Utils;
import ba.unsa.etf.si.utility.image.QRUtils;
import ba.unsa.etf.si.utility.javafx.CustomFXMLLoader;
import ba.unsa.etf.si.utility.javafx.FXMLUtils;
import ba.unsa.etf.si.utility.stream.StreamUtils;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.Start;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ClassTest {




    @Test
    public void testStreamUtilsConverter() {
        assertEquals(StreamUtils.getNumberFromString("Abc"), -1);
        assertEquals(StreamUtils.getNumberFromString("12"), 12);
    }

    @Test
    public void testQRUtils () {
        assertNull(QRUtils.getQRImage("", 10 , 10));
        assertNotNull(QRUtils.getQRImage("TestMyImage", 100, 80));
    }

    @Test
    public void testBase64Utils () {
        try {
            assertNotEquals(Product.getDefaultImage() , Base64Utils.base64ToImageDecoder("data:image/png;base64,/9j/4RiDRXhpZgAATU0AKgA"));
        }
        catch (Exception e) {
            assertTrue(false);
        }
    }

    @Test
    public void testDateConverter () {
        DateConverter dateConverter = new DateConverter();
        assertEquals("", dateConverter.toString(null));
        assertNull(dateConverter.fromString(""));
    }

}