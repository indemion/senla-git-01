package carservice6.ui;

import carservice6.exceptions.OperationProhibitedException;

public class Navigator {
    private Menu currentMenu;

    public Navigator(Menu menu) {
        currentMenu = menu;
    }

    public void printMenu() {
        System.out.println(currentMenu.getTitle());
        int idx = 0;
        for (MenuItem menuItem : currentMenu.getMenuItems()) {
            System.out.printf("[%d] %s%n", idx, menuItem.getTitle());
            idx++;
        }
    }

    public void navigate(Integer index) {
        if (!currentMenu.isIndexValid(index)) {
            System.out.printf("Пункт с индексом %d отсутствует в меню.\n", index);
            return;
        }

        MenuItem currentMenuItem = currentMenu.getMenuItems().get(index);
        try {
            currentMenuItem.doAction();
        } catch (OperationProhibitedException e) {
            System.out.println(e.getMessage());
        }
        if (currentMenuItem.hasNextMenu()) {
            currentMenu = currentMenuItem.getNextMenu();
        }
    }
}
