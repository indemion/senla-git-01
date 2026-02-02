package ru.indemion.carservice.models.order;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.common.AbstractCsvExporter;
import ru.indemion.carservice.common.Period;

@Component
public class OrderCsvExporter extends AbstractCsvExporter<Order> {
    @Override
    protected String getFilenamePrefix() {
        return "orders";
    }

    @Override
    protected String getHeader() {
        return "id,price,status,masterId,garageSpotId,estimatedWorkPeriodStart,estimatedWorkPeriodEnd," +
                "actualWorkPeriodStart,actualWorkPeriodEnd,createdAt,closedAt,cancelledAt,deletedAt";
    }

    @Override
    protected String convert(Order order) {
        Period estimatedWorkPeriod = order.getEstimatedWorkPeriod();
        Period actualWorkPeriod = order.getActualWorkPeriod();
        String line = escapeCsvField(order.getId()) + CSV_SEPARATOR +
                escapeCsvField(order.getPrice()) + CSV_SEPARATOR +
                escapeCsvField(order.getStatus()) + CSV_SEPARATOR +
                escapeCsvField(order.getMasterId()) + CSV_SEPARATOR +
                escapeCsvField(order.getGarageSpotId()) + CSV_SEPARATOR +
                escapeCsvField(estimatedWorkPeriod.getStart()) + CSV_SEPARATOR +
                escapeCsvField(estimatedWorkPeriod.getEnd()) + CSV_SEPARATOR +
                escapeCsvField(actualWorkPeriod != null ? actualWorkPeriod.getStart() : null) + CSV_SEPARATOR +
                escapeCsvField(actualWorkPeriod != null ? actualWorkPeriod.getEnd() : null) + CSV_SEPARATOR +
                escapeCsvField(order.getCreatedAt()) + CSV_SEPARATOR +
                escapeCsvField(order.getClosedAt()) + CSV_SEPARATOR +
                escapeCsvField(order.getCanceledAt()) + CSV_SEPARATOR +
                escapeCsvField(order.getDeletedAt());
        return line;
    }
}
