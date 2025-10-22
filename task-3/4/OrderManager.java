import java.util.ArrayList;
import java.util.List;

public class OrderManager {
    private static int lastOrderNumber = 0;
    private final List<Order> orders = new ArrayList<>();

    public Order createOrder() {
        Order order = new Order(++lastOrderNumber);
        orders.add(order);
        return order;
    }

    public void removeOrder(Order order) {
        orders.remove(order);
    }

    public void closeOrder(Order order) {
        order.close();
    }

    public void cancelOrder(Order order) {
        order.cancel();
    }

    public List<Order> getOrders() {
        return orders;
    }
}
