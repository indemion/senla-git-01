package ru.indemion.carservice.models.order;

import ru.indemion.carservice.AppConfig;
import ru.indemion.carservice.common.OperationProhibitedMessages;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.exceptions.DataAccessException;
import ru.indemion.carservice.exceptions.OperationProhibitedException;
import ru.indemion.carservice.exceptions.ServiceException;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.MasterService;
import ru.indemion.carservice.models.repositories.OrderRepository;
import ru.indemion.di.Container;
import ru.indemion.di.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class OrderService {
    private final OrderRepository orderRepository;
    private final MasterService masterService;
    private final GarageSpotService garageSpotService;
    private final AppConfig appConfig;
    private final Connection connection;

    @Inject
    public OrderService(OrderRepository orderRepository, MasterService masterService,
                        GarageSpotService garageSpotService, AppConfig appConfig, Connection connection) {
        this.orderRepository = orderRepository;
        this.masterService = masterService;
        this.garageSpotService = garageSpotService;
        this.appConfig = appConfig;
        this.connection = connection;
    }


    public Order create(int price, Master master, GarageSpot garageSpot, LocalDateTime estimatedWorkStartDateTime,
                        LocalDateTime estimatedWorkEndDateTime) {
        Order order = new Order(price, master, garageSpot,
                new Period(estimatedWorkStartDateTime, estimatedWorkEndDateTime));
        return orderRepository.save(order);
    }

    public void delete(int id, boolean soft) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getStatus() == OrderStatus.DELETED) return;
            order.setStatus(OrderStatus.DELETED);
            order.setDeletedAt(LocalDateTime.now());
            executeInTransaction(() -> {
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
                order.getActualWorkPeriod().setStart(LocalDateTime.now());
            }
            executeInTransaction(() -> {
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
            executeInTransaction(() -> {
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
            executeInTransaction(() -> {
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
        executeInTransaction(() -> orderRepository.save(orders));
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
        CsvExporter csvExporter = new CsvExporter();
        return csvExporter.exportToPath(path, orderRepository.findAll());
    }

    public void importFromPath(String path) {
        CsvImporter csvImporter = Container.INSTANCE.resolve(CsvImporter.class);
        List<Order> orders = csvImporter.importFromPath(path);
        orderRepository.save(orders);
    }

    private void executeInTransaction(Runnable runnable) {
        try {
            connection.setAutoCommit(false);
            runnable.run();
            connection.commit();
        } catch (DataAccessException | SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new ServiceException("Ошибка отката транзакции", ex);
            }
            throw new ServiceException("Ошибка транзакции", e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Не удалось установить параметр автокоммита в true.");
            }
        }
    }
}
