package ru.indemion.carservice.models.master;

import ru.indemion.carservice.common.AbstractCsvExporter;

public class CsvExporter extends AbstractCsvExporter<Master> {
    @Override
    protected String getFilenamePrefix() {
        return "masters";
    }

    @Override
    protected String getHeader() {
        return "id,firstname,lastname,status,orderAtWorkId";
    }

    @Override
    protected String convert(Master master) {
        String line = escapeCsvField(master.getId()) + CSV_SEPARATOR +
                escapeCsvField(master.getFirstname()) + CSV_SEPARATOR +
                escapeCsvField(master.getLastname()) + CSV_SEPARATOR +
                escapeCsvField(master.getStatus()) + CSV_SEPARATOR +
                escapeCsvField(master.getOrderAtWork() == null ? "" : master.getOrderAtWork().getId());
        return line;
    }
}
