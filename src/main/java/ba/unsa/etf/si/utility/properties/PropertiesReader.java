package ba.unsa.etf.si.utility.properties;

import ba.unsa.etf.si.App;

import java.io.IOException;
import java.util.Properties;

public class PropertiesReader {

    private PropertiesReader() {}

    private static final Properties properties = new Properties();

    static {
        try {
            properties.load(App.class.getResourceAsStream("config/cash-register.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getValue(String key) {
        return properties.getProperty(key);
    }
}
