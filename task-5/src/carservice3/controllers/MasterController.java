package carservice3.controllers;

import carservice3.common.EntityNotFoundException;
import carservice3.common.ScannerDecorator;
import carservice3.common.SortDirection;
import carservice3.common.Util;
import carservice3.models.master.InMemoryMasterManager;
import carservice3.models.master.Master;
import carservice3.models.master.SortCriteria;
import carservice3.models.master.SortParam;
import carservice3.views.MasterView;

import java.util.List;

public class MasterController {
    InMemoryMasterManager masterManager = InMemoryMasterManager.instance();
    MasterView masterView = new MasterView();
    ScannerDecorator scanner = ScannerDecorator.instance();

    public void index() {
        masterView.index(masterManager.getMasters());
    }

    public void filteredIndex() {
        System.out.print("Введите id заказа, для которого хотите найти назначенного мастера: ");
        int id = scanner.nextInt();
        try {
            masterView.show(masterManager.getMasterByOrderId(id));
        } catch (EntityNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sortedIndex() {
        masterView.index(masterManager.getMastersSorted(new SortParam(
                Util.chooseVariant("Выберите критерий сортировки: ", List.of(SortCriteria.values())),
                Util.chooseVariant("Выберите порядок сортировки: ", List.of(SortDirection.values())))));
    }

    public void create() {
        System.out.print("Введите имя мастера: ");
        String firstname = scanner.nextLine();
        System.out.print("Введите фамилию мастера: ");
        String lastname = scanner.nextLine();
        Master master = masterManager.create(firstname, lastname);
        masterView.show(master);
    }

    public void delete() {
        System.out.print("Введите id мастера для удаления: ");
        int id = scanner.nextInt();
        masterManager.remove(id);
        masterView.index(masterManager.getMasters());
    }
}
