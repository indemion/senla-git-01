package carservice4.ui.controllers;

import carservice4.ui.MenuBuilder;
import carservice4.ui.Navigator;

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
        Navigator navigator = new Navigator(new MenuBuilder().buildRootMenu());
        while (true) {
            navigator.printMenu();
        }
    }
}
