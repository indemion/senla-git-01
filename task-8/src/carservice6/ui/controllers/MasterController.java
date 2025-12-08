package carservice6.ui.controllers;

import carservice6.common.SortDirection;
import carservice6.exceptions.EntityNotFoundException;
import carservice6.models.master.*;
import carservice6.ui.ScannerDecorator;
import carservice6.ui.Util;
import carservice6.ui.views.MasterView;

import java.util.List;

public class MasterController {
    MasterService masterService = MasterService.instance();
    MasterView masterView = new MasterView();
    ScannerDecorator scanner = ScannerDecorator.instance();

    public void index() {
        masterView.index(masterService.getMasters());
    }

    public void filteredIndex() {
        System.out.print("Введите id заказа, для которого хотите найти назначенного мастера: ");
        int id = scanner.nextInt();
        try {
            masterView.show(masterService.getMasterByOrderId(id));
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sortedIndex() {
        masterView.index(masterService.getMastersSorted(new SortParam(
                Util.chooseVariant("Выберите критерий сортировки: ", List.of(SortCriteria.values())),
                Util.chooseVariant("Выберите порядок сортировки: ", List.of(SortDirection.values())))));
    }

    public void create() {
        System.out.print("Введите имя мастера: ");
        String firstname = scanner.nextLine();
        System.out.print("Введите фамилию мастера: ");
        String lastname = scanner.nextLine();
        Master master = masterService.create(firstname, lastname);
        masterView.show(master);
    }

    public void delete() {
        System.out.print("Введите id мастера для удаления: ");
        int id = scanner.nextInt();
        masterService.delete(id);
        masterView.index(masterService.getMasters());
    }

    public void exportToPath() {
        String defaultPath = (new CsvExporter()).getDefaultPath();
        System.out.print("Введите путь к файлу в который хотите экспортировать сущности (по умолчанию " + defaultPath + "): ");
        String path = masterService.exportToPath(scanner.nextLine());
        System.out.println("Создан файл " + path);
    }

    public void importFromPath() {
        System.out.print("Введите путь к файлу из которого вы хотите импортировать сущности: ");
        masterService.importFromPath(scanner.nextLine());
    }
}
