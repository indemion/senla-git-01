package carservice.models.order;

import carservice.AppConfig;
import carservice.common.OperationProhibitedMessages;
import carservice.common.Period;
import carservice.exceptions.OperationProhibitedException;
import carservice.models.garage.GarageSpot;
import carservice.models.master.Master;
import carservice.models.repositories.IRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderService {
    private static OrderService instance;
    private static int lastId = 0;
    private final IRepository<Order> orderRepository;

    private OrderService() {
        this.orderRepository = OrderRepository.instance();
    }

    public static OrderService instance() {
        if (instance == null) {
            instance = new OrderService();
        }

        return instance;
    }

    private int getNextId() {
        return ++lastId;
    }

    public Order create(int price, Master master, GarageSpot garageSpot, LocalDateTime estimatedWorkStartDateTime,
                        LocalDateTime estimatedWorkEndDateTime) {
        Order order = new Order(getNextId(), price, master, garageSpot,
                new Period(estimatedWorkStartDateTime, estimatedWorkEndDateTime));
        orderRepository.save(order);

        return order;
    }

    public void delete(int id, boolean soft) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            order.delete();
            if (!soft) {
                orderRepository.delete(id);
            }
        });
    }

    public void softDelete(int id) {
        if (!AppConfig.instance().isOrderRemovable()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_REMOVING);
        }
        delete(id, true);
    }

    public void startWorking(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(Order::startWorking);
    }

    public void closeOrder(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(Order::close);
    }

    public void cancelOrder(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(Order::cancel);
    }

    public void shiftEstimatedWorkPeriodInCreatedOrders(Duration duration) {
        if (!AppConfig.instance().isOrderShiftableEstimatedWorkPeriod()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_SHIFTING_ESTIMATED_WORK_PERIOD);
        }
        for (Order order : orderRepository.findAll()) {
            if (order.getStatus() == OrderStatus.CREATED) {
                order.shiftEstimatedWorkPeriod(duration);
            }
        }
    }

    public Optional<Order> findById(int id) {
        return orderRepository.findById(id);
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

    public List<Order> getOrdersFilteredByGarageSpotInPeriod(GarageSpot spot, Period period) {
        return query().addPredicate(order -> order.getGarageSpot().getNumber() == spot.getNumber() &&
                order.getEstimatedWorkPeriod().isOverlap(period)).get();
    }

    public String exportToPath(String path) {
        CsvExporter csvExporter = new CsvExporter();
        return csvExporter.exportToPath(path, query().get());
    }

    public void importFromPath(String path) {
        CsvImporter csvImporter = new CsvImporter();
        List<Order> orders = csvImporter.importFromPath(path);
        orderRepository.save(orders);
    }

    private Query query() {
        return new Query(orderRepository.findAll());
    }
}
