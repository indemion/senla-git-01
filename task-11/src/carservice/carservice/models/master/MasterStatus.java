package carservice.models.master;

import java.util.Arrays;

public enum MasterStatus {
    FREE,
    BUSY;

    public static MasterStatus parse(String status) {
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
