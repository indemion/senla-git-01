package carservice5.ui.controllers;

import carservice5.ui.MenuBuilder;
import carservice5.ui.Navigator;
import carservice5.ui.ScannerDecorator;

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
            System.out.print("Введите номер меню: ");
            navigator.navigate(ScannerDecorator.instance().nextInt());
        }
    }
}
