package ru.indemion.carservice.models.garage;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.common.AbstractCsvImporter;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.repositories.OrderRepository;

import java.util.Optional;

@Component
public class GarageSpotCsvImporter extends AbstractCsvImporter<GarageSpot> {
    private final OrderRepository orderRepository;

    public GarageSpotCsvImporter(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
        GarageSpotStatus status = GarageSpotStatus.parse(fields[2]);
        Order orderAtWork = null;
        if (!fields[3].isEmpty()) {
            Optional<Order> optionalOrder = orderRepository.findById(Integer.parseInt(fields[3]));
            if (optionalOrder.isPresent()) {
                orderAtWork = optionalOrder.get();
            }
        }

        return new GarageSpot(id, number, status, orderAtWork);
    }
}
