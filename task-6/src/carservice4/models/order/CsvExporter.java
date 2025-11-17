package carservice4.models.order;

import carservice4.common.AbstractCsvExporter;

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
        StringBuilder line = new StringBuilder();
        line.append(escapeCsvField(order.getId())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getPrice())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getStatus())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getMaster().getId())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getGarageSpot().getId())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getEstimatedWorkPeriod().getStart())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getEstimatedWorkPeriod().getEnd())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getActualWorkPeriod().getStart())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getActualWorkPeriod().getEnd())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getCreatedAt())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getClosedAt())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getCanceledAt())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(order.getDeletedAt()));
        return line.toString();
    }
}
