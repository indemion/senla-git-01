package ru.indemion.carservice.common;

import java.util.List;

public abstract class AbstractCsvConverter<T> {
    protected static final String CSV_SEPARATOR = ",";

    protected String escapeCsvField(Object field) {
        return String.valueOf(field == null ? "" : field);
    }

    public String convert(List<T> entities) {
        StringBuilder csv = new StringBuilder();
        csv.append(getHeader()).append("\n");

        entities.forEach(entity -> csv.append(getLine(entity)).append("\n"));
        return csv.toString();
    }

    protected abstract String getHeader();

    protected abstract String getLine(T entity);
}
