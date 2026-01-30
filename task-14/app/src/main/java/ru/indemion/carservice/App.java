package ru.indemion.carservice;

import ru.indemion.carservice.ui.controllers.ConsoleController;
import ru.indemion.di.Inject;

public class App {
    private final ConsoleController consoleController;

    @Inject
    public App(ConsoleController consoleController) {
        this.consoleController = consoleController;
    }

    public void run() {
        consoleController.run();
    }
}
