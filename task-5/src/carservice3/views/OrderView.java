package carservice3.views;

import carservice3.models.order.Order;

import java.util.List;

public class OrderView {
    public void index(List<Order> orders) {
        for (Order order : orders) {
            System.out.println(order);
        }
    }

    public void show(Order order) {
        System.out.println(order);
    }
}
