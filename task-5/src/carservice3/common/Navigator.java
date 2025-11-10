package carservice3.common;

import java.util.Scanner;

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
        Scanner input = new Scanner(System.in);
        String command;
        System.out.print("Введите команду: ");
        command = input.nextLine();
        navigate(Integer.parseInt(command));
    }

    public void navigate(Integer index) {
        if (!currentMenu.isIndexValid(index)) {
            System.out.println("Такого пункта меню нет.");
            return;
        }

        MenuItem currentMenuItem = currentMenu.getMenuItems().get(index);
        currentMenuItem.doAction();
        if (currentMenuItem.hasNextMenu()) {
            currentMenu = currentMenuItem.getNextMenu();
        }
    }
}
