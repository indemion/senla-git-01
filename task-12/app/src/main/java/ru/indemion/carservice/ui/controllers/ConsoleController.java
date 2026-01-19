package ru.indemion.carservice.ui.controllers;

import ru.indemion.carservice.ui.MenuBuilder;
import ru.indemion.carservice.ui.Navigator;
import ru.indemion.carservice.ui.utils.ScannerDecorator;
import ru.indemion.di.Inject;

import java.util.Scanner;

public class ConsoleController {
    private final MenuBuilder menuBuilder;

    @Inject
    public ConsoleController(MenuBuilder menuBuilder) {
        this.menuBuilder = menuBuilder;
    }

    public void run() {
        Navigator navigator = new Navigator(menuBuilder.buildRootMenu());
        try (Scanner scanner = new Scanner(System.in)) {
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
