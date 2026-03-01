package ru.indemion.carservice.models.garage;

import java.util.Arrays;

public enum GarageSpotStatus {
    FREE,
    OCCUPIED;

    public static GarageSpotStatus parse(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status не может быть пустым");
        }
        String normalized = status.trim().toUpperCase();
        try {
            return valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: '" + status + "'. Valid values: " +
                            Arrays.toString(values()));
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
