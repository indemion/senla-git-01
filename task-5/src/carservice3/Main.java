package carservice3;

import carservice3.controllers.ConsoleController;
import carservice3.models.garage.InMemoryGarageSpotManager;
import carservice3.models.master.InMemoryMasterManager;
import carservice3.models.order.InMemoryOrderManager;
import carservice3.seeds.MainSeed;

public class Main {
    public static void main(String[] args) {
        initDependencies();
        MainSeed.run();
        ConsoleController consoleController = ConsoleController.instance();
        consoleController.run();
    }

    private static void initDependencies() {
        InMemoryGarageSpotManager.instance().setOrderManager(InMemoryOrderManager.instance());
        InMemoryGarageSpotManager.instance().setMasterManager(InMemoryMasterManager.instance());
        InMemoryMasterManager.instance().setOrderManager(InMemoryOrderManager.instance());
    }
}