package carservice;

import carservice.common.ExitProcess;
import carservice.ui.controllers.ConsoleController;
import di.Inject;

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
