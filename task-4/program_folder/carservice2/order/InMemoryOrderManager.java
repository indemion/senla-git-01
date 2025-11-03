package carservice2.order;

import carservice2.common.Period;
import carservice2.garage.GarageSpot;
import carservice2.master.Master;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class InMemoryOrderManager {
    private final List<Order> orders = new ArrayList<>();

    public Order create(int price, Master master, GarageSpot garageSpot, LocalDateTime estimatedWorkStartDateTime, LocalDateTime estimatedWorkEndDateTime) {
        Order order = new Order(price, master, garageSpot, new Period(estimatedWorkStartDateTime, estimatedWorkEndDateTime));
        orders.add(order);
        System.out.printf("Создан заказ с номером %d%n", order.getId());

        return order;
    }

    public void remove(Order order) {
        Optional<Order> optionalOrder = findById(order.getId());
        if (optionalOrder.isEmpty()) {
            return;
        }
        order.delete();
        System.out.printf("Удалён заказ с номером %d%n", order.getId());
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

    public void shiftEstimatedWorkPeriodInCreatedOrders(Duration duration) {
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.CREATED) {
                System.out.println("Смещение преполагаемого окна работ для заказа под номером " + order.getId());
                String beforeShiftingPeriod = order.getEstimatedWorkPeriod().toString();
                order.shiftEstimatedWorkPeriod(duration);
                System.out.println("Было: " + beforeShiftingPeriod);
                System.out.println("Стало: " + order.getEstimatedWorkPeriod().toString());
            }
        }
    }

    public List<Order> getOrders() {
        return query().ignoreDeletedEntities().get();
    }

    public List<Order> getOrdersSorted(SortParam sortParam) {
        return query().orderBy(sortParam).get();
    }

    public List<Order> getWIPOrders() {
        return query().filterByStatus(OrderStatus.WORK_IN_PROGRESS).get();
    }

    public List<Order> getWIPOrdersSorted(SortParam sortParam) {
        return query().filterByStatus(OrderStatus.WORK_IN_PROGRESS).orderBy(sortParam).get();
    }

    public List<Order> getOrdersFilteredByStatus(OrderStatus status) {
        return query().filterByStatus(status).get();
    }

    public List<Order> getOrdersFilteredByGarageSpotNumber(int number) {
        return query().filterByGarageSpotNumber(number).get();
    }

    public Order getWIPOrderByMaster(Master master) {
        return master.getOrderAtWork();
    }

    public List<Order> getOrdersByMaster(Master master) {
        return query().filterByMaster(master).get();
    }

    public List<Order> getOrdersByMasterCreatedAndWIP(Master master) {
        OrderStatus[] statuses = {OrderStatus.CREATED, OrderStatus.WORK_IN_PROGRESS};
        return query().filterByMaster(master).addPredicate(order -> {
            return Arrays.stream(statuses).anyMatch(status -> status == order.getStatus());
        }).get();
    }

    public List<Order> getOrdersByMasterAndEstimatedWorkPeriodOverlapPeriod(Master master, Period period) {
        return query().filterByMaster(master).addPredicate(order -> order.getEstimatedWorkPeriod().isOverlap(period))
                .get();
    }

    public List<Order> getOrdersFilteredByStatusInPeriodSorted(OrderStatus status, Period period, SortParam sortParam) {
        return query().filterByStatus(status).filterByStatusInPeriod(status, period).orderBy(sortParam).get();
    }

    public Optional<Order> findById(int id) {
        return orders.stream().filter((o) -> o.getId() == id).findFirst();
    }

    public Query query() {
        return new Query(orders);
    }

    public List<Order> getOrdersFilteredByGarageSpotInPeriod(GarageSpot spot, Period period) {
        return query().addPredicate(order -> order.getGarageSpot().getNumber() == spot.getNumber() &&
                order.getEstimatedWorkPeriod().isOverlap(period)).get();
    }
}
