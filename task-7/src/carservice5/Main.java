package carservice5;

import carservice5.common.ExitProcess;
import carservice5.common.SerializationContainer;
import carservice5.common.SerializationManager;
import carservice5.models.garage.GarageSpotService;
import carservice5.models.master.MasterService;
import carservice5.models.order.OrderService;
import carservice5.seeds.MainSeed;
import carservice5.ui.controllers.ConsoleController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        registerExitHook();
        boolean savedDataRestored = restoreSavedDataIfExist();
        initDependencies();
        if (!savedDataRestored) {
            MainSeed.run();
        }
        ConsoleController.instance().run();
    }

    private static void registerExitHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new ExitProcess()));
    }

    private static void initDependencies() {
        MasterService.instance().setOrderService(OrderService.instance());
        GarageSpotService.instance().setMasterService(MasterService.instance());
    }

    private static boolean restoreSavedDataIfExist() {
        Path filePath = Paths.get(Config.instance().getProperty("saveFile"));
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