package carservice;

import carservice.annotations.Configurator;
import carservice.common.ExitProcess;
import carservice.common.SerializationContainer;
import carservice.common.SerializationManager;
import carservice.models.garage.GarageSpotService;
import carservice.models.master.MasterService;
import carservice.models.order.OrderService;
import carservice.seeds.MainSeed;
import carservice.ui.controllers.ConsoleController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class App {
    public void run() {
        initConfig();
        registerExitHook();
        boolean savedDataRestored = restoreSavedDataIfExist();
        initDependencies();
        if (!savedDataRestored) {
            MainSeed.run();
        }
        ConsoleController.instance().run();
    }

    private void initConfig() {
        AppConfig appConfig = AppConfig.instance();
        Configurator configurator = new Configurator();
        configurator.configure(appConfig);
    }

    private void registerExitHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new ExitProcess()));
    }

    private void initDependencies() {
        MasterService.instance().setOrderService(OrderService.instance());
        GarageSpotService.instance().setMasterService(MasterService.instance());
    }

    private boolean restoreSavedDataIfExist() {
        Path filePath = Paths.get(AppConfig.instance().getSaveFile());
        if (Files.isDirectory(filePath) || !Files.isReadable(filePath)) {
            return false;
        }
        SerializationManager serializationManager = new SerializationManager();
        SerializationContainer serializationContainer = (SerializationContainer) serializationManager
                .deserialize(filePath.toString());
        serializationContainer.restoreReferences();
        return true;
    }
}
