package carservice4.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCsvImporter<T> {
    protected static final String CSV_SEPARATOR = ",";

    public List<T> importFromPath(String path) {
        try(BufferedReader reader = new BufferedReader(new FileReader(path))) {
            List<T> entities = new ArrayList<>();
            // Пропускаем строку заголовков
            String header = reader.readLine();
            if (header.split(",", -1).length != getColumnsCount()) {
                throw new InvalidCsvFileException("Некорректный формат файла для импорта заказов");
            }
            String line;
            while ((line = reader.readLine()) != null) {
                entities.add(createFromCsvString(line));
            }
            return entities;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected LocalDateTime parseLocalDateTime(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(str);
    }

    protected abstract int getColumnsCount();

    protected abstract T createFromCsvString(String str);
}
