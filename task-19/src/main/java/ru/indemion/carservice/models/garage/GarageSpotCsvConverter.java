package ru.indemion.carservice.models.garage;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.common.AbstractCsvConverter;

@Component
public class GarageSpotCsvConverter extends AbstractCsvConverter<GarageSpot> {
    @Override
    protected String getHeader() {
        return "id" + CSV_SEPARATOR +
                "number" + CSV_SEPARATOR +
                "status" + CSV_SEPARATOR +
                "orderAtWorkId";
    }

    @Override
    protected String getLine(GarageSpot garageSpot) {
        String line = escapeCsvField(garageSpot.getId()) + CSV_SEPARATOR +
                escapeCsvField(garageSpot.getNumber()) + CSV_SEPARATOR +
                escapeCsvField(garageSpot.getStatus()) + CSV_SEPARATOR +
                escapeCsvField(garageSpot.getOrderAtWork() == null ? "" : garageSpot.getOrderAtWork().getId());
        return line;
    }
}
