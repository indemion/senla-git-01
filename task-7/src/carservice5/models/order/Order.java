package carservice5.models.order;

import carservice5.common.Period;
import carservice5.models.Entity;
import carservice5.models.garage.GarageSpot;
import carservice5.models.master.Master;
import carservice5.models.order.OrderStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order extends Entity {
    // price в минимальной единице валюты, т.е. в копейках
    private final int price;
    private final Master master;
    private final GarageSpot garageSpot;
    private OrderStatus status;
    private final Period estimatedWorkPeriod;
    private final Period actualWorkPeriod;
    private final LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private LocalDateTime canceledAt;
    private LocalDateTime deletedAt;

    public Order(int id, int price, Master master, GarageSpot garageSpot, Period estimatedWorkPeriod) {
        super(id);
        this.price = price;
        this.master = master;
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
        this.master = master;
        this.garageSpot = garageSpot;
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

    public Master getMaster() {
        return master;
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

    private void setActualEndTime() {
        actualWorkPeriod.setEnd(LocalDateTime.now());
    }

    void shiftEstimatedWorkPeriod(Duration duration) {
        estimatedWorkPeriod.shift(duration);
    }

    void startWorking() {
        if (status == OrderStatus.WORK_IN_PROGRESS) return;
        if (status == OrderStatus.CREATED) {
            actualWorkPeriod.setStart(LocalDateTime.now());
        }
        status = OrderStatus.WORK_IN_PROGRESS;
        master.setOrderAtWork(this);
        garageSpot.setOrderAtWork(this);
    }

    void close() {
        if (status == OrderStatus.CLOSED) return;
        if (status == OrderStatus.WORK_IN_PROGRESS) {
            setActualEndTime();
        }
        status = OrderStatus.CLOSED;
        closedAt = LocalDateTime.now();
        master.setOrderAtWork(null);
        garageSpot.setOrderAtWork(null);
    }

    void cancel() {
        if (status == OrderStatus.CANCELED) return;
        if (status == OrderStatus.WORK_IN_PROGRESS) {
            setActualEndTime();
        }
        status = OrderStatus.CANCELED;
        canceledAt = LocalDateTime.now();
        master.setOrderAtWork(null);
        garageSpot.setOrderAtWork(null);
    }

    void delete() {
        if (status == OrderStatus.DELETED) return;
        status = OrderStatus.DELETED;
        deletedAt = LocalDateTime.now();
        master.setOrderAtWork(null);
        garageSpot.setOrderAtWork(null);
    }

    public GarageSpot getGarageSpot() {
        return garageSpot;
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
                closedAt != null ? canceledAt.format(dateTimePattern) : "null",
                canceledAt != null ? canceledAt.format(dateTimePattern) : "null",
                deletedAt != null ? deletedAt.format(dateTimePattern) : "null");
    }
}
