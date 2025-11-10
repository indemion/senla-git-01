package carservice3.models.garage;

import carservice3.models.order.Order;

public class GarageSpot {
    private final int number;
    private GarageSpotStatus status;
    private Order orderAtWork;

    GarageSpot(int number) {
        this.number = number;
        this.status = GarageSpotStatus.FREE;
    }

    public int getNumber() {
        return number;
    }

    public GarageSpotStatus getStatus() {
        return status;
    }

    public void setOrderAtWork(Order orderAtWork) {
        this.orderAtWork = orderAtWork;
        status = orderAtWork == null ? GarageSpotStatus.FREE : GarageSpotStatus.OCCUPIED;
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
