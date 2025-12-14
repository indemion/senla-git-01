package carservice.ui.controllers;

import carservice.AppConfig;
import carservice.common.OperationProhibitedMessages;
import carservice.exceptions.OperationProhibitedException;
import carservice.models.garage.CsvExporter;
import carservice.models.garage.GarageSpotService;
import carservice.models.garage.GarageSpotStatus;
import carservice.ui.ScannerDecorator;
import carservice.ui.Util;
import carservice.ui.views.GarageSpotView;
import di.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GarageSpotController {
    private final GarageSpotService garageSpotService;
    private final GarageSpotView garageSpotView = new GarageSpotView();
    private final ScannerDecorator scanner = ScannerDecorator.instance();

    @Inject
    public GarageSpotController(GarageSpotService garageSpotService) {
        this.garageSpotService = garageSpotService;
    }

    public void index() {
        garageSpotView.index(garageSpotService.getGarageSpots());
    }

    public void filteredIndex() {
        GarageSpotStatus status = Util.chooseVariant("Выберите статус по которому хотите фильтровать: ",
                List.of(GarageSpotStatus.values()));
        garageSpotView.index(garageSpotService.getGarageSpotsByStatus(status));
    }

    public void freeGarageSpotsCountAtDate() {
        System.out.print("Введите дату в формате (11.11.2011): ");
        LocalDate localDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("d.M.yyyy"));
        System.out.printf("На дату %s, будет свободно мест %d\n", localDate,
                garageSpotService.getFreeGarageSpotsCountAtDate(localDate));
    }

    public void create() {
        System.out.print("Введите номер гаражного места: ");
        int number = scanner.nextInt();
        garageSpotView.show(garageSpotService.create(number));
    }

    public void delete() {
        if (!AppConfig.instance().isOrderRemovable()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.GARAGE_SPOT_REMOVING);
        }
        System.out.print("Введите номер гаражного места: ");
        int number = scanner.nextInt();
        garageSpotService.delete(number);
        garageSpotView.index(garageSpotService.getGarageSpots());
    }

    public void exportToPath() {
        String defaultPath = (new CsvExporter()).getDefaultPath();
        System.out.print("Введите путь к файлу в который хотите экспортировать сущности (по умолчанию " + defaultPath + "): ");
        String path = garageSpotService.exportToPath(scanner.nextLine());
        System.out.println("Создан файл " + path);
    }

    public void importFromPath() {
        System.out.print("Введите путь к файлу из которого вы хотите импортировать сущности: ");
        garageSpotService.importFromPath(scanner.nextLine());
    }
}
