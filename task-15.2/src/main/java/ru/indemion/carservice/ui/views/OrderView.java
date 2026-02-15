package ru.indemion.carservice.ui.views;

import ru.indemion.carservice.models.order.Order;

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
