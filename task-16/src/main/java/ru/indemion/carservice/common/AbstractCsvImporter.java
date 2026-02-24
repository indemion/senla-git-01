package ru.indemion.carservice.common;

import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.exceptions.InvalidCsvFileException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCsvImporter<T> {
    protected static final String CSV_SEPARATOR = ",";

    public List<T> importFromPath(String path) {
        try (FileReader fileReader = new FileReader(path)) {
            return parse(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> importFromMultipartFile(MultipartFile file) {
        try (InputStreamReader inputStreamReader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            return parse(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected List<T> parse(InputStreamReader inputStreamReader) {
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            List<T> entities = new ArrayList<>();
            // Пропускаем строку заголовков
            String header = reader.readLine();
            if (header.split(CSV_SEPARATOR, -1).length != getColumnsCount()) {
                throw new InvalidCsvFileException("Некорректный формат файла для импорта");
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
