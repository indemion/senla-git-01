package ru.indemion.carservice.models.garage;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import ru.indemion.carservice.models.IHasId;
import ru.indemion.carservice.models.order.Order;

@Entity
@Table(name = "garage_spots")
public class GarageSpot implements IHasId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int number;
    @Convert(converter = GarageSpotStatusConverter.class)
    private GarageSpotStatus status;
    @Column(name = "order_at_work_id", insertable = false, updatable = false)
    private Integer orderAtWorkId;
    @OneToOne(fetch = FetchType.LAZY, targetEntity = Order.class)
    @JoinColumn(name = "order_at_work_id")
    private Order orderAtWork;

    public GarageSpot() {
    }

    public GarageSpot(int number) {
        this.number = number;
        this.status = GarageSpotStatus.FREE;
    }

    public GarageSpot(int id, int number, GarageSpotStatus status, Order orderAtWork) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.orderAtWork = orderAtWork;
    }

    public GarageSpot(int id, int number, GarageSpotStatus status, Integer orderAtWorkId) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.orderAtWorkId = orderAtWorkId;
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public GarageSpotStatus getStatus() {
        return status;
    }

    public Integer getOrderAtWorkId() {
        return orderAtWorkId;
    }

    public Order getOrderAtWork() {
        return orderAtWork;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderAtWork(Order orderAtWork) {
        this.orderAtWork = orderAtWork;
        this.status = orderAtWork == null ? GarageSpotStatus.FREE : GarageSpotStatus.OCCUPIED;
    }

    @Override
    public String toString() {
        return "GarageSpot{" +
                "id=" + id +
                ", number=" + number +
                ", status=" + status +
                ", orderAtWorkId=" + orderAtWorkId +
                '}';
    }
}
