package carservice6.ui.controllers;

import carservice6.ui.IAction;

public class ExitAction implements IAction {
    @Override
    public void execute() {
        System.exit(0);
    }
}
