package ru.indemion.carservice.models.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.OperationProhibitedMessages;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.config.AppConfig;
import ru.indemion.carservice.dto.CreateOrderDto;
import ru.indemion.carservice.dto.OrderDto;
import ru.indemion.carservice.exceptions.EntityNotFoundException;
import ru.indemion.carservice.exceptions.OperationProhibitedException;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.MasterService;
import ru.indemion.carservice.models.repositories.OrderRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final MasterService masterService;
    private final GarageSpotService garageSpotService;
    private final AppConfig appConfig;
    private final OrderCsvExporter csvExporter;
    private final OrderCsvImporter csvImporter;
    private final OrderCsvConverter orderCsvConverter;

    public OrderServiceImpl(OrderRepository orderRepository,
                            MasterService masterService,
                            GarageSpotService garageSpotService,
                            AppConfig appConfig,
                            OrderCsvExporter csvExporter, OrderCsvImporter csvImporter,
                            OrderCsvConverter orderCsvConverter) {
        this.orderRepository = orderRepository;
        this.masterService = masterService;
        this.garageSpotService = garageSpotService;
        this.appConfig = appConfig;
        this.csvExporter = csvExporter;
        this.csvImporter = csvImporter;
        this.orderCsvConverter = orderCsvConverter;
    }


    @Override
    public Order create(int price, Master master, GarageSpot garageSpot, LocalDateTime estimatedWorkStartDateTime,
                        LocalDateTime estimatedWorkEndDateTime) {
        Order order = new Order(price, master, garageSpot,
                new Period(estimatedWorkStartDateTime, estimatedWorkEndDateTime));
        return orderRepository.save(order);
    }

    @Override
    public void delete(int id, boolean soft) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getStatus() == OrderStatus.DELETED) return;
            order.setStatus(OrderStatus.DELETED);
            order.setDeletedAt(LocalDateTime.now());
            masterService.freeMaster(order.getMasterId());
            garageSpotService.freeGarageSpot(order.getGarageSpotId());
            if (!soft) {
                orderRepository.delete(order);
            } else {
                orderRepository.save(order);
            }
        });
    }

    @Override
    public void softDelete(int id) {
        if (!appConfig.isOrderRemovable()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_REMOVING);
        }
        delete(id, true);
    }

    @Override
    public void startWorking(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getStatus() == OrderStatus.WORK_IN_PROGRESS) return;
            if (order.getStatus() == OrderStatus.CREATED) {
                order.setActualWorkPeriod(new Period(LocalDateTime.now()));
            }
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
    }

    @Override
    public void closeOrder(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getStatus() == OrderStatus.CLOSED) return;
            if (order.getStatus() == OrderStatus.WORK_IN_PROGRESS) {
                order.getActualWorkPeriod().setEnd(LocalDateTime.now());
            }
            order.setStatus(OrderStatus.CLOSED);
            order.setClosedAt(LocalDateTime.now());
            orderRepository.save(order);
            masterService.freeMaster(order.getMasterId());
            garageSpotService.freeGarageSpot(order.getGarageSpotId());
        });
    }

    @Override
    public void cancelOrder(int id) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        optionalOrder.ifPresent(order -> {
            if (order.getStatus() == OrderStatus.CANCELED) return;
            if (order.getStatus() == OrderStatus.WORK_IN_PROGRESS) {
                order.getActualWorkPeriod().setEnd(LocalDateTime.now());
            }
            order.setStatus(OrderStatus.CANCELED);
            order.setCanceledAt(LocalDateTime.now());
            orderRepository.save(order);
            masterService.freeMaster(order.getMasterId());
            garageSpotService.freeGarageSpot(order.getGarageSpotId());
        });
    }

    @Override
    public void shiftEstimatedWorkPeriodInCreatedOrders(Duration duration) {
        if (!appConfig.isOrderShiftableEstimatedWorkPeriod()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_SHIFTING_ESTIMATED_WORK_PERIOD);
        }
        List<Order> orders = getOrdersFilteredByStatus(OrderStatus.CREATED);
        for (Order order : orders) {
            order.shiftEstimatedWorkPeriod(duration);
        }
        orderRepository.save(orders);
    }

    @Override
    public Optional<Order> findById(int id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> getOrdersSorted(SortParams sortParams) {
        return orderRepository.findFilteredAndSorted(null, sortParams);
    }

    @Override
    public List<Order> getOrdersFilteredByStatus(OrderStatus status) {
        return orderRepository.findFilteredAndSorted(FilterParams.builder().statuses(status).build(), null);
    }

    @Override
    public List<Order> getOrdersByMasterCreatedAndWIP(Master master) {
        FilterParams filterParams = FilterParams.builder().masterId(master.getId())
                .statuses(OrderStatus.CREATED, OrderStatus.WORK_IN_PROGRESS).build();
        return orderRepository.findFilteredAndSorted(filterParams, null);
    }

    @Override
    public List<Order> getOrdersFilteredByStatusInPeriod(OrderStatus status, Period period) {
        FilterParams filterParams = FilterParams.builder().statuses(status).estimatedWorkStartInPeriod(period).build();
        return orderRepository.findFilteredAndSorted(filterParams, null);
    }

    @Override
    public String exportToPath(String path) {
        return csvExporter.exportToPath(path, orderRepository.findAll());
    }

    @Override
    public void importFromPath(String path) {
        List<Order> orders = csvImporter.importFromPath(path);
        orderRepository.save(orders);
    }

    @Override
    public List<OrderDto> findAll(FilterParams filterParams, SortParams sortParams) {
        return orderRepository.findFilteredAndSorted(filterParams, sortParams).stream().map(this::convertEntityToDto)
                .toList();
    }

    @Override
    public OrderDto create(CreateOrderDto createOrderDto) {
        Optional<Master> optionalMaster = masterService.findById(createOrderDto.getMasterId());
        if (optionalMaster.isEmpty()) {
            throw new EntityNotFoundException("Master не найден с id: " + createOrderDto.getMasterId());
        }
        Optional<GarageSpot> optionalGarageSpot = garageSpotService.findById(createOrderDto.getGarageSpotId());
        if (optionalGarageSpot.isEmpty()) {
            throw new EntityNotFoundException("GarageSpot не найден с id: " + createOrderDto.getMasterId());
        }

        Order order = new Order(createOrderDto.getPrice(), optionalMaster.get(), optionalGarageSpot.get(),
                new Period(createOrderDto.getEstimatedWorkPeriodStart(), createOrderDto.getEstimatedWorkPeriodEnd()));
        return convertEntityToDto(orderRepository.save(order));
    }

    @Override
    public void delete(int id) {
        softDelete(id);
    }

    @Override
    public OrderDto start(int id) {
        startWorking(id);
        return convertEntityToDto(findById(id).get());
    }

    @Override
    public OrderDto close(int id) {
        closeOrder(id);
        return convertEntityToDto(findById(id).get());
    }

    @Override
    public OrderDto cancel(int id) {
        cancelOrder(id);
        return convertEntityToDto(findById(id).get());
    }

    @Override
    public void shiftEstimatedWorkPeriod(int duration) {
        shiftEstimatedWorkPeriodInCreatedOrders(Duration.ofHours(duration));
    }

    @Override
    public String getCsvData() {
        return orderCsvConverter.convert(getOrders());
    }

    @Override
    public void importCsv(MultipartFile file) {
        List<Order> orders = csvImporter.importFromMultipartFile(file);
        orderRepository.save(orders);
    }

    private OrderDto convertEntityToDto(Order order) {
        return new OrderDto(order.getId(), order.getPrice(), order.getMasterId(), order.getGarageSpotId(),
                order.getStatus().toString(), order.getEstimatedWorkPeriod().getStart(),
                order.getEstimatedWorkPeriod().getEnd(),
                order.getActualWorkPeriod() != null ? order.getActualWorkPeriod().getStart() : null,
                order.getActualWorkPeriod() != null ? order.getActualWorkPeriod().getEnd() : null,
                order.getCreatedAt(), order.getClosedAt(), order.getCanceledAt(), order.getDeletedAt());
    }
}
