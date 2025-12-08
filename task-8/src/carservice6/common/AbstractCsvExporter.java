package carservice6.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class AbstractCsvExporter<T> {
    protected static final String CSV_SEPARATOR = ",";

    public String exportToPath(String path, List<T> entities) {
        if (path.isEmpty()) {
            path = getDefaultPath();
        }
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(getHeader());
            writer.newLine();

            for (T entity : entities) {
                writer.write(convert(entity));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return path;
    }

    protected String escapeCsvField(Object field) {
        return String.valueOf(field == null ? "" : field);
    }

    protected String getFilenameWithExtension() {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        return getFilenamePrefix() + "_" + timestamp.format(timestampFormatter) + ".csv";
    }

    public String getDefaultPath() {
        return "." + File.separator + getFilenameWithExtension();
    }

    protected abstract String getFilenamePrefix();
    protected abstract String getHeader();
    protected abstract String convert(T entity);
}
