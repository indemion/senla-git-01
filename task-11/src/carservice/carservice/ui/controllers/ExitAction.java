package carservice.ui.controllers;

import carservice.ui.IAction;

public class ExitAction implements IAction {
    @Override
    public void execute() {
        System.exit(0);
    }
}
