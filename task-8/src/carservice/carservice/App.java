package carservice;

import carservice.common.DataInitializer;
import carservice.common.ExitProcess;
import carservice.common.SerializationContainer;
import carservice.common.SerializationManager;
import carservice.seeds.MainSeed;
import carservice.ui.controllers.ConsoleController;
import di.Inject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    private final AppConfig appConfig;
    private final SerializationManager serializationManager;
    private final ConsoleController consoleController;
    private final DataInitializer dataInitializer;
    private final ExitProcess exitProcess;

    @Inject
    public App(AppConfig appConfig, SerializationManager serializationManager, ConsoleController consoleController,
               DataInitializer dataInitializer, ExitProcess exitProcess) {
        this.appConfig = appConfig;
        this.serializationManager = serializationManager;
        this.consoleController = consoleController;
        this.dataInitializer = dataInitializer;
        this.exitProcess = exitProcess;
    }

    public void run() {
        registerExitHook();
        boolean savedDataRestored = restoreSavedDataIfExist();
        if (!savedDataRestored) {
            MainSeed.run();
        }
        consoleController.run();
    }

    private void registerExitHook() {
        Runtime.getRuntime().addShutdownHook(exitProcess);
    }

    private boolean restoreSavedDataIfExist() {
        Path filePath = Paths.get(appConfig.getSaveFile());
        if (Files.isDirectory(filePath) || !Files.isReadable(filePath)) {
            return false;
        }
        SerializationContainer serializationContainer = serializationManager.load();
        dataInitializer.init(serializationContainer);

        return true;
    }
}
