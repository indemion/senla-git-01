package ru.indemion.carservice.ui.controllers;

import ru.indemion.carservice.common.SortDirection;
import ru.indemion.carservice.models.master.CsvExporter;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.MasterService;
import ru.indemion.carservice.models.master.SortCriteria;
import ru.indemion.carservice.models.master.SortParams;
import ru.indemion.carservice.ui.utils.OperationLogger;
import ru.indemion.carservice.ui.utils.ScannerDecorator;
import ru.indemion.carservice.ui.utils.Util;
import ru.indemion.carservice.ui.views.MasterView;
import ru.indemion.di.Inject;

import java.util.List;
import java.util.Optional;

public class MasterController {
    private final OperationLogger operationLogger;
    MasterService masterService;
    MasterView masterView = new MasterView();
    ScannerDecorator scanner = ScannerDecorator.instance();

    @Inject
    public MasterController(MasterService masterService, OperationLogger operationLogger) {
        this.masterService = masterService;
        this.operationLogger = operationLogger;
    }

    public void index() {
        operationLogger.log("получение списка мастеров", () -> {
            masterView.index(masterService.getMasters());
        });
    }

    public void filteredIndex() {
        operationLogger.log("получение фильтрованного списка мастеров", () -> {
            System.out.print("Введите id заказа, для которого хотите найти назначенного мастера: ");
            int id = scanner.nextInt();
            Optional<Master> optionalMaster = masterService.getMasterByOrderId(id);
            if (optionalMaster.isEmpty()) {
                System.out.println("Мастер с id заказа = " + id + ", не найден.");
                return;
            }
            masterView.show(optionalMaster.get());
        });
    }

    public void sortedIndex() {
        operationLogger.log("получение сортированного списка мастеров", () -> {
            masterView.index(masterService.getMastersSorted(new SortParams(
                    Util.chooseVariant("Выберите критерий сортировки: ", List.of(SortCriteria.values())),
                    Util.chooseVariant("Выберите порядок сортировки: ", List.of(SortDirection.values())))));
        });
    }

    public void create() {
        operationLogger.log("создание мастера", () -> {
            System.out.print("Введите имя мастера: ");
            String firstname = scanner.nextLine();
            System.out.print("Введите фамилию мастера: ");
            String lastname = scanner.nextLine();
            Master master = masterService.create(firstname, lastname);
            masterView.show(master);
        });
    }

    public void delete() {
        operationLogger.log("удаление мастера", () -> {
            System.out.print("Введите id мастера для удаления: ");
            int id = scanner.nextInt();
            masterService.delete(id);
            masterView.index(masterService.getMasters());
        });
    }

    public void exportToPath() {
        operationLogger.log("экспорт списка мастеров в файл", () -> {
            String defaultPath = (new CsvExporter()).getDefaultPath();
            System.out.print("Введите путь к файлу в который хотите экспортировать сущности (по умолчанию " + defaultPath + "): ");
            String path = masterService.exportToPath(scanner.nextLine());
            System.out.println("Создан файл " + path);
        });
    }

    public void importFromPath() {
        operationLogger.log("импорт списка мастеров из файла", () -> {
            System.out.print("Введите путь к файлу из которого вы хотите импортировать сущности: ");
            masterService.importFromPath(scanner.nextLine());
        });
    }
}
