package carservice2.garage;

import carservice2.order.Order;

public class GarageSpot {
    private final int number;
    private Order orderAtWork;

    GarageSpot(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setOrderAtWork(Order orderAtWork) {
        this.orderAtWork = orderAtWork;
    }

    public boolean isEmpty() {
        return orderAtWork == null;
    }

    @Override
    public String toString() {
        return String.format("GarageSpot:\n - number: %d", number);
    }
}
