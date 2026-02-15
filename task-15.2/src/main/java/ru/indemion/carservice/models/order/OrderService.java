package ru.indemion.carservice.models.order;

import org.springframework.stereotype.Service;
import ru.indemion.carservice.AppConfig;
import ru.indemion.carservice.common.OperationProhibitedMessages;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.exceptions.OperationProhibitedException;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.MasterService;
import ru.indemion.carservice.models.repositories.OrderRepository;
import ru.indemion.carservice.models.services.AbstractTransactionalService;
import ru.indemion.carservice.util.HibernateUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService extends AbstractTransactionalService {
    private final OrderRepository orderRepository;
    private final MasterService masterService;
    private final GarageSpotService garageSpotService;
    private final AppConfig appConfig;
    private final OrderCsvExporter csvExporter;
    private final OrderCsvImporter csvImporter;

    public OrderService(OrderRepository orderRepository,
                        MasterService masterService,
                        GarageSpotService garageSpotService,
                        AppConfig appConfig,
                        OrderCsvExporter csvExporter, OrderCsvImporter csvImporter) {
        super(HibernateUtil.getCurrentSession());
        this.orderRepository = orderRepository;
        this.masterService = masterService;
        this.garageSpotService = garageSpotService;
        this.appConfig = appConfig;
        this.csvExporter = csvExporter;
        this.csvImporter = csvImporter;
    }


    public Order create(int price, Master master, GarageSpot garageSpot, LocalDateTime estimatedWorkStartDateTime,
                        LocalDateTime estimatedWorkEndDateTime) {
        Order order = new Order(price, master, garageSpot,
                new Period(estimatedWorkStartDateTime, estimatedWorkEndDateTime));
        return inTransaction(() -> orderRepository.save(order));
    }

    public void delete(int id, boolean soft) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getStatus() == OrderStatus.DELETED) return;
            order.setStatus(OrderStatus.DELETED);
            order.setDeletedAt(LocalDateTime.now());
            inTransaction(() -> {
                masterService.freeMaster(order.getMasterId());
                garageSpotService.freeGarageSpot(order.getGarageSpotId());
                if (!soft) {
                    orderRepository.delete(order);
                } else {
                    orderRepository.save(order);
                }
            });
        });
    }

    public void softDelete(int id) {
        if (!appConfig.isOrderRemovable()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_REMOVING);
        }
        delete(id, true);
    }

    public void startWorking(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getStatus() == OrderStatus.WORK_IN_PROGRESS) return;
            if (order.getStatus() == OrderStatus.CREATED) {
                order.setActualWorkPeriod(new Period(LocalDateTime.now()));
            }
            inTransaction(() -> {
                order.setStatus(OrderStatus.WORK_IN_PROGRESS);
                orderRepository.save(order);
                Optional<Master> optionalMaster = masterService.findById(order.getMasterId());
                optionalMaster.ifPresent(master -> {
                    master.setOrderAtWork(order);
                    masterService.save(master);
                });
                Optional<GarageSpot> optionalGarageSpot = garageSpotService.findById(order.getGarageSpotId());
                optionalGarageSpot.ifPresent(garageSpot -> {
                    garageSpot.setOrderAtWork(order);
                    garageSpotService.save(garageSpot);
                });
            });
        });
    }

    public void closeOrder(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getStatus() == OrderStatus.CLOSED) return;
            if (order.getStatus() == OrderStatus.WORK_IN_PROGRESS) {
                order.getActualWorkPeriod().setEnd(LocalDateTime.now());
            }
            order.setStatus(OrderStatus.CLOSED);
            order.setClosedAt(LocalDateTime.now());
            inTransaction(() -> {
                orderRepository.save(order);
                masterService.freeMaster(order.getMasterId());
                garageSpotService.freeGarageSpot(order.getGarageSpotId());
            });
        });
    }

    public void cancelOrder(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getStatus() == OrderStatus.CANCELED) return;
            if (order.getStatus() == OrderStatus.WORK_IN_PROGRESS) {
                order.getActualWorkPeriod().setEnd(LocalDateTime.now());
            }
            order.setStatus(OrderStatus.CANCELED);
            order.setCanceledAt(LocalDateTime.now());
            inTransaction(() -> {
                orderRepository.save(order);
                masterService.freeMaster(order.getMasterId());
                garageSpotService.freeGarageSpot(order.getGarageSpotId());
            });
        });
    }

    public void shiftEstimatedWorkPeriodInCreatedOrders(Duration duration) {
        if (!appConfig.isOrderShiftableEstimatedWorkPeriod()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_SHIFTING_ESTIMATED_WORK_PERIOD);
        }
        List<Order> orders = getOrdersFilteredByStatus(OrderStatus.CREATED);
        for (Order order : orders) {
            order.shiftEstimatedWorkPeriod(duration);
        }
        inTransaction(() -> orderRepository.save(orders));
    }

    public Optional<Order> findById(int id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    public List<Order> getOrdersSorted(SortParams sortParams) {
        return orderRepository.findFilteredAndSorted(null, sortParams);
    }

    public List<Order> getOrdersFilteredByStatus(OrderStatus status) {
        return orderRepository.findFilteredAndSorted(FilterParams.builder().statuses(status).build(), null);
    }

    public List<Order> getOrdersByMasterCreatedAndWIP(Master master) {
        FilterParams filterParams = FilterParams.builder().masterId(master.getId())
                .statuses(OrderStatus.CREATED, OrderStatus.WORK_IN_PROGRESS).build();
        return orderRepository.findFilteredAndSorted(filterParams, null);
    }

    public List<Order> getOrdersFilteredByStatusInPeriod(OrderStatus status, Period period) {
        FilterParams filterParams = FilterParams.builder().statuses(status).estimatedWorkStartInPeriod(period).build();
        return orderRepository.findFilteredAndSorted(filterParams, null);
    }

    public String exportToPath(String path) {
        return csvExporter.exportToPath(path, orderRepository.findAll());
    }

    public void importFromPath(String path) {
        List<Order> orders = csvImporter.importFromPath(path);
        inTransaction(() -> orderRepository.save(orders));
    }
}
