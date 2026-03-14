package ru.indemion.carservice.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Util {
    public static String generateFileName(String prefix) {
        LocalDateTime timestamp = LocalDateTime.now();
        DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss");
        return prefix + "_" + timestamp.format(timestampFormatter) + ".csv";
    }

    public static ResponseEntity<byte[]> getResponseEntityForCsvData(String csvData, String fileNamePrefix) {
        byte[] csvBytes = csvData.getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", Util.generateFileName(fileNamePrefix));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
}
