package carservice6.models.garage;

import carservice6.common.AbstractCsvExporter;

public class CsvExporter extends AbstractCsvExporter<GarageSpot> {
    @Override
    protected String getFilenamePrefix() {
        return "garagespots";
    }

    @Override
    protected String getHeader() {
        return "id,number,status,orderAtWorkId";
    }

    @Override
    protected String convert(GarageSpot garageSpot) {
        StringBuilder line = new StringBuilder();
        line.append(escapeCsvField(garageSpot.getId())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(garageSpot.getNumber())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(garageSpot.getStatus())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(garageSpot.getOrderAtWork() == null ? "" : garageSpot.getOrderAtWork().getId()));
        return line.toString();
    }
}
