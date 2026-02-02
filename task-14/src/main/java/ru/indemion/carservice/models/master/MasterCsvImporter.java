package ru.indemion.carservice.models.master;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.common.AbstractCsvImporter;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.repositories.OrderRepository;

import java.util.Optional;

@Component
public class MasterCsvImporter extends AbstractCsvImporter<Master> {
    private final OrderRepository orderRepository;

    public MasterCsvImporter(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
            Optional<Order> optionalOrder = orderRepository.findById(Integer.parseInt(fields[4]));
            if (optionalOrder.isPresent()) {
                orderAtWork = optionalOrder.get();
            }
        }

        return new Master(id, firstname, lastname, status, orderAtWork);
    }
}
