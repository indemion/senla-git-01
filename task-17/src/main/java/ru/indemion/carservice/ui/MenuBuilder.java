package ru.indemion.carservice.ui;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.ui.controllers.ExitAction;
import ru.indemion.carservice.ui.controllers.GarageSpotController;
import ru.indemion.carservice.ui.controllers.MasterController;
import ru.indemion.carservice.ui.controllers.OrderController;

import java.util.ArrayList;
import java.util.List;

@Component
public class MenuBuilder {
    private final MasterController masterController;
    private final GarageSpotController garageSpotController;
    private final OrderController orderController;

    public MenuBuilder(OrderController orderController, MasterController masterController,
                       GarageSpotController garageSpotController) {
        this.orderController = orderController;
        this.masterController = masterController;
        this.garageSpotController = garageSpotController;
    }

    public Menu buildRootMenu() {
        Menu menu = new Menu("Главное меню");
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Мастера", masterController::index, buildMastersMenu(menu)));
        menuItems.add(new MenuItem("Гаражные места", garageSpotController::index, buildGarageSpotsMenu(menu)));
        menuItems.add(new MenuItem("Заказы", orderController::index, buildOrdersMenu(menu)));
        menuItems.add(new MenuItem("Выход", new ExitAction()));
        menu.addMenuItems(menuItems);
        return menu;
    }

    private Menu buildMastersMenu(Menu rootMenu) {
        Menu menu = new Menu("Меню мастеров");
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Экспортировать", masterController::exportToPath));
        menuItems.add(new MenuItem("Импортировать", masterController::importFromPath));
        menuItems.add(new MenuItem("Фильтровать", masterController::filteredIndex));
        menuItems.add(new MenuItem("Сортировать", masterController::sortedIndex));
        menuItems.add(new MenuItem("Добавить", masterController::create));
        menuItems.add(new MenuItem("Удалить", masterController::delete));
        menuItems.add(new MenuItem("Назад", () -> {
        }, rootMenu));
        menu.addMenuItems(menuItems);
        return menu;
    }

    private Menu buildGarageSpotsMenu(Menu rootMenu) {
        Menu menu = new Menu("Меню гаражных мест");
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Экспортировать", garageSpotController::exportToPath));
        menuItems.add(new MenuItem("Импортировать", garageSpotController::importFromPath));
        menuItems.add(new MenuItem("Фильтровать", garageSpotController::filteredIndex));
        menuItems.add(new MenuItem("Кол-во свободных мест на дату", garageSpotController::freeGarageSpotsCountAtDate));
        menuItems.add(new MenuItem("Добавить", garageSpotController::create));
        menuItems.add(new MenuItem("Удалить", garageSpotController::delete));
        menuItems.add(new MenuItem("Назад", () -> {
        }, rootMenu));
        menu.addMenuItems(menuItems);
        return menu;
    }

    private Menu buildOrdersMenu(Menu rootMenu) {
        Menu menu = new Menu("Меню заказов");
        List<MenuItem> menuItems = new ArrayList<>();
        menuItems.add(new MenuItem("Экспортировать", orderController::exportToPath));
        menuItems.add(new MenuItem("Импортировать", orderController::importFromPath));
        menuItems.add(new MenuItem("Фильтровать", orderController::filteredIndex));
        menuItems.add(new MenuItem("Сортировать", orderController::sortedIndex));
        menuItems.add(new MenuItem("Начать работу", orderController::startWorking));
        menuItems.add(new MenuItem("Закрыть", orderController::close));
        menuItems.add(new MenuItem("Отменить", orderController::cancel));
        menuItems.add(new MenuItem("Добавить", orderController::create));
        menuItems.add(new MenuItem("Удалить", orderController::delete));
        menuItems.add(new MenuItem("Сдвинуть время выполнения", orderController::shiftOrdersEstimatedWorkPeriod));
        menuItems.add(new MenuItem("Назад", () -> {
        }, rootMenu));
        menu.addMenuItems(menuItems);
        return menu;
    }
}
