package carservice3.controllers;

import carservice3.common.Builder;
import carservice3.common.Navigator;

public class ConsoleController {
    private static ConsoleController instance;

    private ConsoleController() {
    }

    public static ConsoleController instance() {
        if (instance == null) {
            instance = new ConsoleController();
        }

        return instance;
    }

    public void run() {
        Navigator navigator = new Navigator(new Builder().buildRootMenu());
        while (true) {
            navigator.printMenu();
        }
    }
}
