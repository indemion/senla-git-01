package ru.indemion.carservice.models.garage;

import ru.indemion.carservice.common.AbstractCsvImporter;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.order.OrderService;
import ru.indemion.di.Inject;

import java.util.Optional;

public class CsvImporter extends AbstractCsvImporter<GarageSpot> {
    private final OrderService orderService;

    @Inject
    public CsvImporter(OrderService orderService) {
        this.orderService = orderService;
    }

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
