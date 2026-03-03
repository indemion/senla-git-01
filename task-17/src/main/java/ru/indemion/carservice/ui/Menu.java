package ru.indemion.carservice.ui;

import java.util.ArrayList;
import java.util.List;

public class Menu {
    private final String title;
    private final List<MenuItem> menuItems = new ArrayList<>();

    public Menu(String title) {
        this.title = title;
    }

    public List<MenuItem> getMenuItems() {
        return new ArrayList<>(menuItems);
    }

    public void addMenuItems(List<MenuItem> menuItems) {
        this.menuItems.addAll(menuItems);
    }

    public String getTitle() {
        return title;
    }

    public boolean isIndexValid(Integer index) {
        return index >= 0 && index < menuItems.size();
    }
}
