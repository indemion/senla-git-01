package carservice.ui.controllers;

import carservice.common.SortDirection;
import carservice.models.master.*;
import carservice.ui.ScannerDecorator;
import carservice.ui.Util;
import carservice.ui.views.MasterView;
import di.Inject;

import java.util.List;
import java.util.Optional;

public class MasterController {
    MasterService masterService;
    MasterView masterView = new MasterView();
    ScannerDecorator scanner = ScannerDecorator.instance();

    @Inject
    public MasterController(MasterService masterService) {
        this.masterService = masterService;
    }

    public void index() {
        masterView.index(masterService.getMasters());
    }

    public void filteredIndex() {
        System.out.print("Введите id заказа, для которого хотите найти назначенного мастера: ");
        int id = scanner.nextInt();
        Optional<Master> optionalMaster = masterService.getMasterByOrderId(id);
        if (optionalMaster.isEmpty()) {
            System.out.println("Мастер с id заказа = " + id + ", не найден.");
            return;
        }
        masterView.show(optionalMaster.get());
    }

    public void sortedIndex() {
        masterView.index(masterService.getMastersSorted(new SortParams(
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
