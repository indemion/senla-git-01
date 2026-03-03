package ru.indemion.carservice.models.master;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.common.AbstractCsvConverter;

@Component
public class MasterCsvConverter extends AbstractCsvConverter<Master> {
    @Override
    protected String getHeader() {
        return "id" + CSV_SEPARATOR +
                "firstname" + CSV_SEPARATOR +
                "lastname" + CSV_SEPARATOR +
                "status" + CSV_SEPARATOR +
                "orderAtWorkId";
    }

    @Override
    protected String getLine(Master master) {
        String line = escapeCsvField(master.getId()) + CSV_SEPARATOR +
                escapeCsvField(master.getFirstname()) + CSV_SEPARATOR +
                escapeCsvField(master.getLastname()) + CSV_SEPARATOR +
                escapeCsvField(master.getStatus()) + CSV_SEPARATOR +
                escapeCsvField(master.getOrderAtWork() == null ? "" : master.getOrderAtWork().getId());
        return line;
    }
}
