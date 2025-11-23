package carservice5;

import carservice5.models.garage.GarageSpotService;
import carservice5.models.master.MasterService;
import carservice5.models.order.OrderService;
import carservice5.seeds.MainSeed;
import carservice5.ui.controllers.ConsoleController;

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