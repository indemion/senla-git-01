package carservice.models.order;

import carservice.common.Period;
import carservice.models.Entity;
import carservice.models.garage.GarageSpot;
import carservice.models.master.Master;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Order extends Entity {
    // price в минимальной единице валюты, т.е. в копейках
    private final int price;
    private final int masterId;
    private transient Master master;
    private final int garageSpotId;
    private transient GarageSpot garageSpot;
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
        this.masterId = master.getId();
        this.master = master;
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
        this.master = master;
        this.garageSpotId = garageSpot.getId();
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

    public int getMasterId() {
        return masterId;
    }

    public Master getMaster() {
        return master;
    }

    public void setMaster(Master master) {
        if (master.getId() != masterId) {
            // TODO: возможно стоит бросать исключение
            return;
        }
        this.master = master;
    }

    public int getGarageSpotId() {
        return garageSpotId;
    }

    public void setGarageSpot(GarageSpot garageSpot) {
        if (garageSpot.getId() != garageSpotId) {
            // TODO: возможно стоит бросать исключение
            return;
        }
        this.garageSpot = garageSpot;
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
