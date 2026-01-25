package ru.indemion.carservice.models.order;

import ru.indemion.carservice.common.AbstractCsvExporter;

public class CsvExporter extends AbstractCsvExporter<Order> {
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
        String line = escapeCsvField(order.getId()) + CSV_SEPARATOR +
                escapeCsvField(order.getPrice()) + CSV_SEPARATOR +
                escapeCsvField(order.getStatus()) + CSV_SEPARATOR +
                escapeCsvField(order.getMasterId()) + CSV_SEPARATOR +
                escapeCsvField(order.getGarageSpotId()) + CSV_SEPARATOR +
                escapeCsvField(order.getEstimatedWorkPeriod().getStart()) + CSV_SEPARATOR +
                escapeCsvField(order.getEstimatedWorkPeriod().getEnd()) + CSV_SEPARATOR +
                escapeCsvField(order.getActualWorkPeriod().getStart()) + CSV_SEPARATOR +
                escapeCsvField(order.getActualWorkPeriod().getEnd()) + CSV_SEPARATOR +
                escapeCsvField(order.getCreatedAt()) + CSV_SEPARATOR +
                escapeCsvField(order.getClosedAt()) + CSV_SEPARATOR +
                escapeCsvField(order.getCanceledAt()) + CSV_SEPARATOR +
                escapeCsvField(order.getDeletedAt());
        return line;
    }
}
