package carservice5;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Config instance;
    private final Properties properties = new Properties();

    private Config() {
        loadProperties();
    }

    public static Config instance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    private void loadProperties() {
        try (FileReader propertiesReader = new FileReader("./config.properties")) {
            properties.load(propertiesReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProperty(String propertyName) {
        return properties.getProperty(propertyName);
    }

    public Boolean getBooleanProperty(String propertyName) {
        return Boolean.parseBoolean(getProperty(propertyName));
    }
}
