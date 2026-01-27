package ru.indemion.carservice.models.master;

import ru.indemion.carservice.common.AbstractCsvImporter;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.order.OrderService;

import java.util.Optional;

public class CsvImporter extends AbstractCsvImporter<Master> {
    private final OrderService orderService;

    public CsvImporter(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    protected int getColumnsCount() {
        return 5;
    }

    @Override
    protected Master createFromCsvString(String str) {
        String[] fields = str.split(CSV_SEPARATOR, -1);
        int id = Integer.parseInt(fields[0]);
        String firstname = fields[1];
        String lastname = fields[2];
        MasterStatus status = MasterStatus.valueOf(fields[3]);
        Order orderAtWork = null;
        if (!fields[4].isEmpty()) {
            Optional<Order> optionalOrder = orderService.findById(Integer.parseInt(fields[4]));
            if (optionalOrder.isPresent()) {
                orderAtWork = optionalOrder.get();
            }
        }

        return new Master(id, firstname, lastname, status, orderAtWork);
    }
}
