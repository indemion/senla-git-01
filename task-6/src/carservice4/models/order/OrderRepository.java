package carservice4.models.order;

import carservice4.models.repositories.InMemoryRepository;

public class OrderRepository extends InMemoryRepository<Order> {
    private static OrderRepository instance;

    private OrderRepository() {
    }

    public static OrderRepository instance() {
        if (instance == null) {
            instance = new OrderRepository();
        }

        return instance;
    }
}
