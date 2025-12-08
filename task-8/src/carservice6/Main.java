package carservice6;

import carservice6.annotations.Configurator;
import carservice6.common.ExitProcess;
import carservice6.common.SerializationContainer;
import carservice6.common.SerializationManager;
import carservice6.models.garage.GarageSpotService;
import carservice6.models.master.MasterService;
import carservice6.models.order.OrderService;
import carservice6.seeds.MainSeed;
import carservice6.ui.controllers.ConsoleController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        initConfig();
        registerExitHook();
        boolean savedDataRestored = restoreSavedDataIfExist();
        initDependencies();
        if (!savedDataRestored) {
            MainSeed.run();
        }
        ConsoleController.instance().run();
    }

    private static void initConfig() {
        AppConfig appConfig = AppConfig.instance();
        Configurator configurator = new Configurator();
        configurator.configure(appConfig);
    }

    private static void registerExitHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new ExitProcess()));
    }

    private static void initDependencies() {
        MasterService.instance().setOrderService(OrderService.instance());
        GarageSpotService.instance().setMasterService(MasterService.instance());
    }

    private static boolean restoreSavedDataIfExist() {
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