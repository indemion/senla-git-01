package carservice5;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class App {
    private static App instance;
    private final Properties properties = new Properties();

    private App() {
        loadProperties();
    }

    public static App instance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    private void loadProperties() {
        try (FileReader propertiesReader = new FileReader("task-7/src/carservice5/config.properties")) {
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
