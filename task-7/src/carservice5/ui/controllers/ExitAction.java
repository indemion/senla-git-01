package carservice5.ui.controllers;

import carservice5.ui.IAction;

public class ExitAction implements IAction {
    @Override
    public void execute() {
        System.exit(0);
    }
}
