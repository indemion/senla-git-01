package ru.indemion.carservice.models.order;

import ru.indemion.carservice.common.AbstractCsvImporter;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.exceptions.CsvImportException;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.MasterService;
import ru.indemion.di.Inject;

import java.time.LocalDateTime;
import java.util.Optional;

public class CsvImporter extends AbstractCsvImporter<Order> {
    private final MasterService masterService;
    private final GarageSpotService garageSpotService;

    @Inject
    public CsvImporter(MasterService masterService, GarageSpotService garageSpotService) {
        this.masterService = masterService;
        this.garageSpotService = garageSpotService;
    }

    @Override
    public Order createFromCsvString(String str) {
        String[] fields = str.split(CSV_SEPARATOR, -1);
        int id = Integer.parseInt(fields[0]);
        int price = Integer.parseInt(fields[1]);
        OrderStatus status = OrderStatus.parse(fields[2]);
        int masterId = Integer.parseInt(fields[3]);
        Optional<Master> optionalMaster = masterService.findById(masterId);
        if (optionalMaster.isEmpty()) {
            throw new CsvImportException("Невозможно добавить заказ с id: " + id + " так как в системе нет мастера с " +
                    "указанным в заказе id");
        }
        int garageSpotId = Integer.parseInt(fields[4]);
        Optional<GarageSpot> optionalGarageSpot = garageSpotService.findById(garageSpotId);
        if (optionalGarageSpot.isEmpty()) {
            throw new CsvImportException("Невозможно добавить заказ с id: " + id + " так как в системе нет гаражного " +
                    "места с указанным id");
        }
        LocalDateTime estimatedWorkPeriodStart = parseLocalDateTime(fields[5]);
        LocalDateTime estimatedWorkPeriodEnd = parseLocalDateTime(fields[6]);
        Period estimatedWorkPeriod = Period.newInstance(estimatedWorkPeriodStart, estimatedWorkPeriodEnd);
        LocalDateTime actualWorkPeriodStart = parseLocalDateTime(fields[7]);
        LocalDateTime actualWorkPeriodEnd = parseLocalDateTime(fields[8]);
        Period actualWorkPeriod = Period.newInstance(actualWorkPeriodStart, actualWorkPeriodEnd);
        LocalDateTime createdAt = parseLocalDateTime(fields[9]);
        LocalDateTime closedAt = parseLocalDateTime(fields[10]);
        LocalDateTime canceledAt = parseLocalDateTime(fields[11]);
        LocalDateTime deletedAt = parseLocalDateTime(fields[12]);

        return new Order(id, price, status, masterId, garageSpotId, estimatedWorkPeriod, actualWorkPeriod, createdAt,
                closedAt, canceledAt, deletedAt);
    }

    @Override
    protected int getColumnsCount() {
        return 13;
    }
}
