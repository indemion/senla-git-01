package carservice3.controllers;

import carservice3.common.ScannerDecorator;
import carservice3.common.Util;
import carservice3.models.garage.GarageSpotStatus;
import carservice3.models.garage.InMemoryGarageSpotManager;
import carservice3.views.GarageSpotView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GarageSpotController {
    private final InMemoryGarageSpotManager garageSpotManager = InMemoryGarageSpotManager.instance();
    private final GarageSpotView garageSpotView = new GarageSpotView();
    private final ScannerDecorator scanner = ScannerDecorator.instance();

    public void index() {
        garageSpotView.index(garageSpotManager.getGarageSpots());
    }

    public void filteredIndex() {
        GarageSpotStatus status = Util.chooseVariant("Выберите статус по которому хотите фильтровать: ",
                List.of(GarageSpotStatus.values()));
        garageSpotView.index(garageSpotManager.getGarageSpotsByStatus(status));
    }

    public void freeGarageSpotsCountAtDate() {
        System.out.print("Введите дату в формате (11.11.2011): ");
        LocalDate localDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("d.M.yyyy"));
        System.out.printf("На дату %s, будет свободно мест %d\n", localDate,
                garageSpotManager.getFreeGarageSpotsCountAtDate(localDate));;
    }

    public void create() {
        System.out.print("Введите номер гаражного места: ");
        int number = scanner.nextInt();
        garageSpotView.show(garageSpotManager.create(number));
    }

    public void delete() {
        System.out.print("Введите номер гаражного места: ");
        int number = scanner.nextInt();
        garageSpotManager.remove(number);
        garageSpotView.index(garageSpotManager.getGarageSpots());
    }
}
