package carservice.ui.controllers;

import carservice.ui.MenuBuilder;
import carservice.ui.Navigator;
import carservice.ui.ScannerDecorator;

import java.util.Scanner;

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
        try(Scanner scanner = new Scanner(System.in)) {
            ScannerDecorator.instance().setScanner(scanner);
            while (true) {
                navigator.printMenu();
                System.out.print("Введите номер меню: ");
                // TODO: разобраться почему сканнер кидает ошибку при завершении программы комбинацией CTRL+C
                navigator.navigate(ScannerDecorator.instance().nextInt());
            }
        }
    }
}
