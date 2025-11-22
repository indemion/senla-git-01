package carservice4.models.master;

import carservice4.common.AbstractCsvExporter;

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
        StringBuilder line = new StringBuilder();
        line.append(escapeCsvField(master.getId())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(master.getFirstname())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(master.getLastname())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(master.getStatus())).append(CSV_SEPARATOR);
        line.append(escapeCsvField(master.getOrderAtWork() == null ? "" : master.getOrderAtWork().getId()));
        return line.toString();
    }
}
