package carservice6.models.garage;

import carservice6.common.AbstractCsvImporter;
import carservice6.models.order.Order;
import carservice6.models.order.OrderService;

import java.util.Optional;

public class CsvImporter extends AbstractCsvImporter<GarageSpot> {
    private final OrderService orderService = OrderService.instance();

    @Override
    protected int getColumnsCount() {
        return 4;
    }

    @Override
    protected GarageSpot createFromCsvString(String str) {
        String[] fields = str.split(CSV_SEPARATOR, -1);
        int id = Integer.parseInt(fields[0]);
        int number = Integer.parseInt(fields[1]);
        GarageSpotStatus status = GarageSpotStatus.valueOf(fields[2]);
        Order orderAtWork = null;
        if (!fields[3].isEmpty()) {
            Optional<Order> optionalOrder = orderService.findById(Integer.parseInt(fields[3]));
            if (optionalOrder.isPresent()) {
                orderAtWork = optionalOrder.get();
            }
        }

        return new GarageSpot(id, number, status, orderAtWork);
    }
}
