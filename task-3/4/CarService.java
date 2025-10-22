import java.util.List;

public class CarService {
    private final Garage garage = new Garage();
    private final MasterManager masterManager = new MasterManager();
    private final OrderManager orderManager = new OrderManager();

    public void addGarageSpot(GarageSpot spot) {
        garage.addSpot(spot);
    }

    public void removeGarageSpot(GarageSpot spot) {
        garage.removeSpot(spot);
    }

    public void addMaster(Master master) {
        masterManager.addMaster(master);
    }

    public void removeMaster(Master master) {
        masterManager.removeMaster(master);
    }

    public Order createOrder() {
        return orderManager.createOrder();
    }

    public void removeOrder(Order order) {
        orderManager.removeOrder(order);
    }

    public List<Order> getOrders() {
        return orderManager.getOrders();
    }

    public void closeOrder(Order order) {
        orderManager.closeOrder(order);
    }

    public void cancelOrder(Order order) {
        orderManager.cancelOrder(order);
    }
}
