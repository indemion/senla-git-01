package carservice5.models.garage;

import carservice5.models.Entity;
import carservice5.models.order.Order;

public class GarageSpot extends Entity {
    private final int number;
    private GarageSpotStatus status;
    private Integer orderAtWorkId;
    private transient Order orderAtWork;

    GarageSpot(int id, int number) {
        super(id);
        this.number = number;
        this.status = GarageSpotStatus.FREE;
    }

    public GarageSpot(int id, int number, GarageSpotStatus status, Order orderAtWork) {
        super(id);
        this.number = number;
        this.status = status;
        this.orderAtWorkId = orderAtWork.getId();
        this.orderAtWork = orderAtWork;
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

    public void setOrderAtWork(Order orderAtWork) {
        this.orderAtWork = orderAtWork;
        this.orderAtWorkId = orderAtWork == null ? null : orderAtWork.getId();
        this.status = orderAtWork == null ? GarageSpotStatus.FREE : GarageSpotStatus.OCCUPIED;
    }

    public Order getOrderAtWork() {
        return orderAtWork;
    }

    @Override
    public String toString() {
        return String.format("""
                GarageSpot:
                - number: %d
                - status: %s
                - orderAtWorkId: %s""", number, status, orderAtWork == null ? "null" : orderAtWork.getId());
    }
}
