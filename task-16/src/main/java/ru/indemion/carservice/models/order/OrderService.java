package ru.indemion.carservice.models.order;

import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.dto.CreateOrderDto;
import ru.indemion.carservice.dto.OrderDto;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.master.Master;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order create(int price, Master master, GarageSpot garageSpot, LocalDateTime estimatedWorkStartDateTime,
                 LocalDateTime estimatedWorkEndDateTime);

    void delete(int id, boolean soft);

    void softDelete(int id);

    void startWorking(int id);

    void closeOrder(int id);

    void cancelOrder(int id);

    void shiftEstimatedWorkPeriodInCreatedOrders(Duration duration);

    Optional<Order> findById(int id);

    List<Order> getOrders();

    List<Order> getOrdersSorted(SortParams sortParams);

    List<Order> getOrdersFilteredByStatus(OrderStatus status);

    List<Order> getOrdersByMasterCreatedAndWIP(Master master);

    List<Order> getOrdersFilteredByStatusInPeriod(OrderStatus status, Period period);

    String exportToPath(String path);

    void importFromPath(String path);

    List<OrderDto> findAll(FilterParams filterParams, SortParams sortParams);

    OrderDto create(CreateOrderDto createOrderDto);

    void delete(int id);

    OrderDto start(int id);

    OrderDto close(int id);

    OrderDto cancel(int id);

    void shiftEstimatedWorkPeriod(int duration);

    String getCsvData();

    void importCsv(MultipartFile file);
}
