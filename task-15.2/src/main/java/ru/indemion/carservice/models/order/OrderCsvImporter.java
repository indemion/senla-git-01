package ru.indemion.carservice.models.order;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.common.AbstractCsvImporter;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.exceptions.CsvImportException;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.repositories.GarageSpotRepository;
import ru.indemion.carservice.models.repositories.MasterRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class OrderCsvImporter extends AbstractCsvImporter<Order> {


    private final MasterRepository masterRepository;
    private final GarageSpotRepository garageSpotRepository;

    public OrderCsvImporter(MasterRepository masterRepository, GarageSpotRepository garageSpotRepository) {
        this.masterRepository = masterRepository;
        this.garageSpotRepository = garageSpotRepository;
    }

    @Override
    public Order createFromCsvString(String str) {
        String[] fields = str.split(CSV_SEPARATOR, -1);
        int id = Integer.parseInt(fields[0]);
        int price = Integer.parseInt(fields[1]);
        OrderStatus status = OrderStatus.parse(fields[2]);
        int masterId = Integer.parseInt(fields[3]);
        Optional<Master> optionalMaster = masterRepository.findById(masterId);
        if (optionalMaster.isEmpty()) {
            throw new CsvImportException("Невозможно добавить заказ с id: " + id + " так как в системе нет мастера с " +
                    "указанным в заказе id");
        }
        int garageSpotId = Integer.parseInt(fields[4]);
        Optional<GarageSpot> optionalGarageSpot = garageSpotRepository.findById(garageSpotId);
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

        return new Order(id, price, status, optionalMaster.get(), optionalGarageSpot.get(), estimatedWorkPeriod,
                actualWorkPeriod, createdAt, closedAt, canceledAt, deletedAt);
    }

    @Override
    protected int getColumnsCount() {
        return 13;
    }
}
