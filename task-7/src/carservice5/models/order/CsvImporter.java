package carservice5.models.order;

import carservice5.common.AbstractCsvImporter;
import carservice5.common.CsvImportException;
import carservice5.common.Period;
import carservice5.models.garage.GarageSpot;
import carservice5.models.garage.GarageSpotService;
import carservice5.models.master.Master;
import carservice5.models.master.MasterService;

import java.time.LocalDateTime;
import java.util.Optional;

public class CsvImporter extends AbstractCsvImporter<Order> {
    private final MasterService masterService = MasterService.instance();
    private final GarageSpotService garageSpotService = GarageSpotService.instance();

    @Override
    public Order createFromCsvString(String str) {
        String[] fields = str.split(CSV_SEPARATOR, -1);
        int id = Integer.parseInt(fields[0]);
        int price = Integer.parseInt(fields[1]);
        OrderStatus status = OrderStatus.valueOf(fields[2]);
        Optional<Master> optionalMaster = masterService.findById(Integer.parseInt(fields[3]));
        if (optionalMaster.isEmpty()) {
            throw new CsvImportException("Невозможно добавить заказ с id: " + id + " так как в системе нет мастера с " +
                    "указанным в заказе id");
        }
        Optional<GarageSpot> optionalGarageSpot = garageSpotService.findById(Integer.parseInt(fields[4]));
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
