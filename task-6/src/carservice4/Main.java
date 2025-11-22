package carservice4;

import carservice4.models.garage.GarageSpotService;
import carservice4.models.master.MasterService;
import carservice4.models.order.OrderService;
import carservice4.seeds.MainSeed;
import carservice4.ui.controllers.ConsoleController;

public class Main {
    public static void main(String[] args) {
        initDependencies();
        MainSeed.run();
        ConsoleController consoleController = ConsoleController.instance();
        consoleController.run();
    }

    private static void initDependencies() {
        MasterService.instance().setOrderService(OrderService.instance());
        GarageSpotService.instance().setMasterService(MasterService.instance());
    }
}