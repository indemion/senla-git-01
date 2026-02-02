package ru.indemion.carservice.models.garage;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.common.AbstractCsvExporter;

@Component
public class GarageSpotCsvExporter extends AbstractCsvExporter<GarageSpot> {
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
        String line = escapeCsvField(garageSpot.getId()) + CSV_SEPARATOR +
                escapeCsvField(garageSpot.getNumber()) + CSV_SEPARATOR +
                escapeCsvField(garageSpot.getStatus()) + CSV_SEPARATOR +
                escapeCsvField(garageSpot.getOrderAtWork() == null ? "" : garageSpot.getOrderAtWork().getId());
        return line;
    }
}
