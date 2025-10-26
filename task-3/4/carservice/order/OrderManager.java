package carservice.order;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderManager {
    private final List<Order> orders = new ArrayList<>();

    public List<Order> getOrders() {
        return orders;
    }

    public void createOrder(LocalDateTime estimatedWorkStartDateTime, LocalDateTime estimatedWorkEndDateTime) {
        Order order = new Order(new TimeWindow(estimatedWorkStartDateTime, estimatedWorkEndDateTime));
        orders.add(order);
        System.out.printf("Добавлен заказ с номером %d%n", order.getId());
    }

    public void removeOrder(int id) {
        Optional<Order> order = orders.stream().filter(o -> o.getId() == id).findFirst();
        if (order.isEmpty()) {
            return;
        }
        orders.remove(order.get());
        System.out.printf("Удалён заказ с номером %d%n", id);
    }

    public void startWorking(Order order) {
        order.startWorking();
        System.out.printf("Принят в работу заказ с номером %d%n", order.getId());
    }

    public void closeOrder(Order order) {
        order.close();
        System.out.printf("Закрыт заказ с номером %d%n", order.getId());
    }

    public void cancelOrder(Order order) {
        order.cancel();
        System.out.printf("Отменён заказ с номером %d%n", order.getId());
    }

    public void shiftEstimatedWorkTimeWindowInCreatedOrders(Duration duration) {
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.CREATED) {
                System.out.println("Смещение преполагаемого окна работ для заказа под номером " + order.getId());
                String beforeShiftingTimeWindow = order.getEstimatedWorkTimeWindow().toString();
                order.shiftEstimatedWorkTimeWindow(duration);
                System.out.println("Было: " + beforeShiftingTimeWindow);
                System.out.println("Стало: " + order.getEstimatedWorkTimeWindow().toString());
            }
        }
    }
}
