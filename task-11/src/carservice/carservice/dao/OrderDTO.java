package carservice.dao;

import java.time.LocalDateTime;

public record OrderDTO(int id, int price, int masterId, int garageSpotId, String status,
                       LocalDateTime estimatedWorkPeriodStart, LocalDateTime estimatedWorkPeriodEnd,
                       LocalDateTime actualWorkPeriodStart, LocalDateTime actualWorkPeriodEnd,
                       LocalDateTime createdAt, LocalDateTime closedAt, LocalDateTime canceledAt,
                       LocalDateTime deletedAt) {
}
