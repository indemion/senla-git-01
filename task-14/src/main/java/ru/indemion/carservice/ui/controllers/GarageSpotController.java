package ru.indemion.carservice.ui.controllers;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.AppConfig;
import ru.indemion.carservice.common.OperationProhibitedMessages;
import ru.indemion.carservice.exceptions.OperationProhibitedException;
import ru.indemion.carservice.models.garage.GarageSpotCsvExporter;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.garage.GarageSpotStatus;
import ru.indemion.carservice.models.services.AvailabilityService;
import ru.indemion.carservice.ui.utils.OperationLogger;
import ru.indemion.carservice.ui.utils.ScannerDecorator;
import ru.indemion.carservice.ui.utils.Util;
import ru.indemion.carservice.ui.views.GarageSpotView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class GarageSpotController {
    private final GarageSpotService garageSpotService;
    private final AvailabilityService availabilityService;
    private final AppConfig appConfig;
    private final GarageSpotView garageSpotView = new GarageSpotView();
    private final ScannerDecorator scanner = ScannerDecorator.instance();
    private final OperationLogger operationLogger;

    public GarageSpotController(GarageSpotService garageSpotService, AvailabilityService availabilityService,
                                AppConfig appConfig, OperationLogger operationLogger) {
        this.garageSpotService = garageSpotService;
        this.availabilityService = availabilityService;
        this.appConfig = appConfig;
        this.operationLogger = operationLogger;
    }

    public void index() {
        operationLogger.log("получение списка гаражных мест", () -> {
            garageSpotView.index(garageSpotService.getGarageSpots());
        });
    }

    public void filteredIndex() {
        operationLogger.log("получение фильтрованного списка гаражных мест", () -> {
            GarageSpotStatus status = Util.chooseVariant("Выберите статус по которому хотите фильтровать: ",
                    List.of(GarageSpotStatus.values()));
            garageSpotView.index(garageSpotService.getGarageSpotsByStatus(status));
        });
    }

    public void freeGarageSpotsCountAtDate() {
        operationLogger.log("подсчёт кол-ва свободных мест на указанную дату", () -> {
            System.out.print("Введите дату в формате (11.11.2011): ");
            LocalDate localDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("d.M.yyyy"));
            System.out.printf("На дату %s, будет свободно мест %d\n", localDate,
                    availabilityService.countAvailableSlotsAtDate(localDate));
        });
    }

    public void create() {
        operationLogger.log("создание гаражного места", () -> {
            System.out.print("Введите номер гаражного места: ");
            int number = scanner.nextInt();
            garageSpotView.show(garageSpotService.create(number));
        });
    }

    public void delete() {
        operationLogger.log("удаление гаражного места", () -> {
            if (!appConfig.isGarageSpotRemovable()) {
                throw new OperationProhibitedException(OperationProhibitedMessages.GARAGE_SPOT_REMOVING);
            }
            System.out.print("Введите номер гаражного места: ");
            int number = scanner.nextInt();
            garageSpotService.delete(number);
            garageSpotView.index(garageSpotService.getGarageSpots());
        });
    }

    public void exportToPath() {
        operationLogger.log("экспорт списка гаражных мест в файл", () -> {
            String defaultPath = (new GarageSpotCsvExporter()).getDefaultPath();
            System.out.print("Введите путь к файлу в который хотите экспортировать сущности (по умолчанию " + defaultPath + "): ");
            String path = garageSpotService.exportToPath(scanner.nextLine());
            System.out.println("Создан файл " + path);
        });
    }

    public void importFromPath() {
        operationLogger.log("импорт списка гаражных мест из файла", () -> {
            System.out.print("Введите путь к файлу из которого вы хотите импортировать сущности: ");
            garageSpotService.importFromPath(scanner.nextLine());
        });
    }
}
