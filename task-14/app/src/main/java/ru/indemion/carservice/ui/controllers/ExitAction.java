package ru.indemion.carservice.ui.controllers;

import ru.indemion.carservice.ui.IAction;

public class ExitAction implements IAction {
    @Override
    public void execute() {
        System.exit(0);
    }
}
