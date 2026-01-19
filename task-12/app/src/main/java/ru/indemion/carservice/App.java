package ru.indemion.carservice;

import ru.indemion.carservice.common.ExitProcess;
import ru.indemion.carservice.ui.controllers.ConsoleController;
import ru.indemion.di.Inject;

public class App {
    private final ConsoleController consoleController;
    private final ExitProcess exitProcess;

    @Inject
    public App(ConsoleController consoleController, ExitProcess exitProcess) {
        this.consoleController = consoleController;
        this.exitProcess = exitProcess;
    }

    public void run() {
        registerExitHook();
        consoleController.run();
    }

    private void registerExitHook() {
        Runtime.getRuntime().addShutdownHook(exitProcess);
    }
}
