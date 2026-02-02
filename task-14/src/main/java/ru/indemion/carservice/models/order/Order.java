package ru.indemion.carservice.models.order;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.IHasId;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.master.Master;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@AttributeOverrides({
        @AttributeOverride(
                name = "estimatedWorkPeriod.start",
                column = @Column(name = "estimated_work_period_start")
        ),
        @AttributeOverride(
                name = "actualWorkPeriod.start",
                column = @Column(name = "actual_work_period_start")
        ),
        @AttributeOverride(
                name = "estimatedWorkPeriod.end",
                column = @Column(name = "estimated_work_period_end")
        ),
        @AttributeOverride(
                name = "actualWorkPeriod.end",
                column = @Column(name = "actual_work_period_end")
        )
})
@Table(name = "orders")
public class Order implements IHasId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int price;
    @Column(name = "master_id", insertable = false, updatable = false, nullable = false)
    private int masterId;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = Master.class)
    @JoinColumn(name = "master_id")
    private Master master;
    @Column(name = "garage_spot_id", insertable = false, updatable = false, nullable = false)
    private int garageSpotId;
    @ManyToOne(fetch = FetchType.LAZY, targetEntity = GarageSpot.class)
    @JoinColumn(name = "garage_spot_id")
    private GarageSpot garageSpot;
    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status;
    private Period estimatedWorkPeriod;
    private Period actualWorkPeriod;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public Order() {
    }

    public Order(int price, Master master, GarageSpot garageSpot, Period estimatedWorkPeriod) {
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
        this.id = id;
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

    public int getId() {
        return id;
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

    public int getGarageSpotId() {
        return garageSpotId;
    }

    public GarageSpot getGarageSpot() {
        return garageSpot;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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

    public void setActualWorkPeriod(Period actualWorkPeriod) {
        this.actualWorkPeriod = actualWorkPeriod;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", price=" + price +
                ", masterId=" + masterId +
                ", garageSpotId=" + garageSpotId +
                ", status=" + status +
                ", estimatedWorkPeriod=" + estimatedWorkPeriod +
                ", actualWorkPeriod=" + actualWorkPeriod +
                ", createdAt=" + localDateTimeToString(createdAt) +
                ", closedAt=" + localDateTimeToString(closedAt) +
                ", canceledAt=" + localDateTimeToString(canceledAt) +
                ", deletedAt=" + localDateTimeToString(deletedAt) +
                '}';
    }

    private String localDateTimeToString(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "null";
        }
        DateTimeFormatter dateTimePattern = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return localDateTime.format(dateTimePattern);
    }
}
