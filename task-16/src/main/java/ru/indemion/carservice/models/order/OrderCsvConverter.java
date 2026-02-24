package ru.indemion.carservice.models.order;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.common.AbstractCsvConverter;
import ru.indemion.carservice.common.Period;

@Component
public class OrderCsvConverter extends AbstractCsvConverter<Order> {
    @Override
    protected String getHeader() {
        return "id" + CSV_SEPARATOR +
                "price" + CSV_SEPARATOR +
                "status" + CSV_SEPARATOR +
                "masterId" + CSV_SEPARATOR +
                "garageSpotId" + CSV_SEPARATOR +
                "estimatedWorkPeriodStart" + CSV_SEPARATOR +
                "estimatedWorkPeriodEnd" + CSV_SEPARATOR +
                "actualWorkPeriodStart" + CSV_SEPARATOR +
                "actualWorkPeriodEnd" + CSV_SEPARATOR +
                "createdAt" + CSV_SEPARATOR +
                "closedAt" + CSV_SEPARATOR +
                "cancelledAt" + CSV_SEPARATOR +
                "deletedAt";
    }

    @Override
    protected String getLine(Order order) {
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
