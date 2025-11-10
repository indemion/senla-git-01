package carservice3.models.order;

import carservice3.common.Period;
import carservice3.models.garage.GarageSpot;
import carservice3.models.master.Master;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InMemoryOrderManager {
    private static InMemoryOrderManager instance;
    private final List<Order> orders = new ArrayList<>();

    private InMemoryOrderManager() {
    }

    public static InMemoryOrderManager instance() {
        if (instance == null) {
            instance = new InMemoryOrderManager();
        }

        return instance;
    }

    public Order create(int price, Master master, GarageSpot garageSpot, LocalDateTime estimatedWorkStartDateTime, LocalDateTime estimatedWorkEndDateTime) {
        Order order = new Order(price, master, garageSpot, new Period(estimatedWorkStartDateTime, estimatedWorkEndDateTime));
        orders.add(order);

        return order;
    }

    public void remove(int id) {
        Optional<Order> optionalOrder = findById(id);
        optionalOrder.ifPresent(Order::delete);
    }

    public void startWorking(int id) {
        Optional<Order> optionalOrder = findById(id);
        optionalOrder.ifPresent(Order::startWorking);
    }

    public void closeOrder(int id) {
        Optional<Order> optionalOrder = findById(id);
        optionalOrder.ifPresent(Order::close);
    }

    public void cancelOrder(int id) {
        Optional<Order> optionalOrder = findById(id);
        optionalOrder.ifPresent(Order::cancel);
    }

    public void shiftEstimatedWorkPeriodInCreatedOrders(Duration duration) {
        for (Order order : orders) {
            if (order.getStatus() == OrderStatus.CREATED) {
                order.shiftEstimatedWorkPeriod(duration);
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
        return query().filterByMaster(master).filterByStatuses(OrderStatus.CREATED, OrderStatus.WORK_IN_PROGRESS).get();
    }

    public List<Order> getOrdersByMasterAndEstimatedWorkPeriodOverlapPeriod(Master master, Period period) {
        return query().filterByMaster(master)
                .filterByStatuses(OrderStatus.CREATED, OrderStatus.WORK_IN_PROGRESS)
                .addPredicate(order -> order.getEstimatedWorkPeriod().isOverlap(period)).get();
    }

    public List<Order> getOrdersFilteredByStatusInPeriod(OrderStatus status, Period period) {
        return query().filterByStatus(status).filterByStatusInPeriod(status, period).get();
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
