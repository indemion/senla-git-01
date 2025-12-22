package carservice.common;

import carservice.AppConfig;
import di.Inject;

import java.io.*;

public class SerializationManager {
    private final String filename;

    @Inject
    public SerializationManager(AppConfig appConfig) {
        this.filename = appConfig.getSaveFile();
    }

    public void save(SerializationContainer serializationContainer) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(serializationContainer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SerializationContainer load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (SerializationContainer) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
