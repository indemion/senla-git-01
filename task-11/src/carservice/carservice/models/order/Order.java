package carservice.models.order;

import carservice.common.Period;
import carservice.models.Model;
import carservice.models.garage.GarageSpot;
import carservice.models.master.Master;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order extends Model {
    // price в минимальной единице валюты, т.е. в копейках
    private final int price;
    private final int masterId;
    private final int garageSpotId;
    private transient GarageSpot garageSpot;
    private OrderStatus status;
    private final Period estimatedWorkPeriod;
    private final Period actualWorkPeriod;
    private final LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime deletedAt;

    public Order(int price, Master master, GarageSpot garageSpot, Period estimatedWorkPeriod) {
        super(0);
        this.price = price;
        this.masterId = master.getId();
        this.garageSpotId = garageSpot.getId();
        this.garageSpot = garageSpot;
        this.status = OrderStatus.CREATED;
        this.estimatedWorkPeriod = estimatedWorkPeriod;
        this.actualWorkPeriod = new Period();
        this.createdAt = LocalDateTime.now();
    }

    public Order(int id, int price, OrderStatus status, Master master, GarageSpot garageSpot,
                 Period estimatedWorkPeriod, Period actualWorkPeriod, LocalDateTime createdAt, LocalDateTime closedAt,
                 LocalDateTime canceledAt, LocalDateTime deletedAt) {
        super(id);
        this.price = price;
        this.status = status;
        this.masterId = master.getId();
        this.garageSpotId = garageSpot.getId();
        this.garageSpot = garageSpot;
        this.estimatedWorkPeriod = estimatedWorkPeriod;
        this.actualWorkPeriod = actualWorkPeriod;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.canceledAt = canceledAt;
        this.deletedAt = deletedAt;
    }

    public Order(int id, int price, OrderStatus status, int masterId, int garageSpotId, Period estimatedWorkPeriod,
                 Period actualWorkPeriod, LocalDateTime createdAt, LocalDateTime closedAt, LocalDateTime canceledAt,
                 LocalDateTime deletedAt) {
        super(id);
        this.price = price;
        this.status = status;
        this.masterId = masterId;
        this.garageSpotId = garageSpotId;
        this.estimatedWorkPeriod = estimatedWorkPeriod;
        this.actualWorkPeriod = actualWorkPeriod;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.canceledAt = canceledAt;
        this.deletedAt = deletedAt;
    }

    public int getPrice() {
        return price;
    }

    public int getMasterId() {
        return masterId;
    }

    public int getGarageSpotId() {
        return garageSpotId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Period getEstimatedWorkPeriod() {
        return estimatedWorkPeriod;
    }

    public Period getActualWorkPeriod() {
        return actualWorkPeriod;
    }

    void shiftEstimatedWorkPeriod(Duration duration) {
        estimatedWorkPeriod.shift(duration);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    @Override
    public String toString() {
        DateTimeFormatter dateTimePattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return String.format("""
                        Order:
                         - id: %d
                         - price: %d
                         - status: %s
                         - estimatedWorkPeriod: %s
                         - actualWorkPeriod: %s
                         - createdAt: %s
                         - closedAt: %s
                         - canceledAt: %s
                         - deletedAt: %s""",
                id, price, status, estimatedWorkPeriod, actualWorkPeriod, createdAt.format(dateTimePattern),
                closedAt != null ? closedAt.format(dateTimePattern) : "null",
                canceledAt != null ? canceledAt.format(dateTimePattern) : "null",
                deletedAt != null ? deletedAt.format(dateTimePattern) : "null");
    }
}
