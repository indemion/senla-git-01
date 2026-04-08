package ru.indemion.carservice.models.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.indemion.carservice.common.OperationProhibitedMessages;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.common.SortDirection;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MasterService masterService;
    @Mock
    private GarageSpotService garageSpotService;
    @Mock
    private AppConfig appConfig;

    @Spy
    @InjectMocks
    private OrderServiceImpl orderService;

    // ==================== delete(int id, boolean soft) ====================
    @Test
    void delete_softDelete_shouldMarkAsDeletedAndFreeResources() {
        int orderId = 1;
        Order order = createOrder(orderId, OrderStatus.CREATED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.delete(orderId, true);

        assertEquals(OrderStatus.DELETED, order.getStatus());
        assertNotNull(order.getDeletedAt());
        verify(masterService).freeMaster(order.getMasterId());
        verify(garageSpotService).freeGarageSpot(order.getGarageSpotId());
        verify(orderRepository).save(order);
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void delete_hardDelete_shouldRemoveFromDatabase() {
        int orderId = 2;
        Order order = createOrder(orderId, OrderStatus.CREATED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.delete(orderId, false);

        verify(masterService).freeMaster(order.getMasterId());
        verify(garageSpotService).freeGarageSpot(order.getGarageSpotId());
        verify(orderRepository).delete(order);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void delete_shouldDoNothingWhenOrderNotFound() {
        int orderId = 999;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        orderService.delete(orderId, true);

        verify(masterService, never()).freeMaster(anyInt());
        verify(garageSpotService, never()).freeGarageSpot(anyInt());
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderRepository, never()).delete(any());
    }

    @Test
    void delete_shouldDoNothingWhenOrderAlreadyDeleted() {
        int orderId = 3;
        Order order = createOrder(orderId, OrderStatus.DELETED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.delete(orderId, true);

        assertEquals(OrderStatus.DELETED, order.getStatus());
        verify(masterService, never()).freeMaster(anyInt());
        verify(garageSpotService, never()).freeGarageSpot(anyInt());
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderRepository, never()).delete(any());
    }

    // ==================== softDelete(int id) ====================
    @Test
    void softDelete_shouldCallDeleteWithSoftTrueWhenAllowed() {
        when(appConfig.isOrderRemovable()).thenReturn(true);
        int orderId = 10;
        Order order = createOrder(orderId, OrderStatus.CREATED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.softDelete(orderId);

        verify(orderRepository).save(any(Order.class));
        verify(masterService).freeMaster(order.getMasterId());
        verify(garageSpotService).freeGarageSpot(order.getGarageSpotId());
    }

    @Test
    void softDelete_shouldThrowExceptionWhenRemovalDisabled() {
        when(appConfig.isOrderRemovable()).thenReturn(false);
        int orderId = 5;

        OperationProhibitedException ex = assertThrows(
                OperationProhibitedException.class,
                () -> orderService.softDelete(orderId)
        );
        assertEquals(OperationProhibitedMessages.ORDER_REMOVING, ex.getMessage());
        verify(orderRepository, never()).findById(anyInt());
    }

    @Test
    void startWorking_shouldStartWorkForCreatedOrder() {
        int orderId = 7;
        Order order = createOrder(orderId, OrderStatus.CREATED);
        order.setActualWorkPeriod(null);
        Master master = new Master("Ivan", "Ivanov");
        master.setId(order.getMasterId());
        GarageSpot spot = new GarageSpot(100);
        spot.setId(order.getGarageSpotId());
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(masterService.findById(order.getMasterId())).thenReturn(Optional.of(master));
        when(garageSpotService.findById(order.getGarageSpotId())).thenReturn(Optional.of(spot));

        orderService.startWorking(orderId);

        assertEquals(OrderStatus.WORK_IN_PROGRESS, order.getStatus());
        assertNotNull(order.getActualWorkPeriod());
        assertNotNull(order.getActualWorkPeriod().getStart());
        assertNull(order.getActualWorkPeriod().getEnd());
        verify(orderRepository).save(order);
        verify(masterService).save(master);
        verify(garageSpotService).save(spot);
        assertSame(order, master.getOrderAtWork());
        assertSame(order, spot.getOrderAtWork());
    }

    @Test
    void startWorking_shouldDoNothingIfAlreadyInProgress() {
        int orderId = 8;
        Order order = createOrder(orderId, OrderStatus.WORK_IN_PROGRESS);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.startWorking(orderId);

        assertEquals(OrderStatus.WORK_IN_PROGRESS, order.getStatus());
        verify(orderRepository, never()).save(any(Order.class));
        verify(masterService, never()).save(any());
        verify(garageSpotService, never()).save(any());
    }

    @Test
    void startWorking_shouldDoNothingWhenOrderNotFound() {
        int orderId = 999;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        orderService.startWorking(orderId);

        verify(orderRepository, never()).save(any(Order.class));
        verify(masterService, never()).save(any());
        verify(garageSpotService, never()).save(any());
    }

    @Test
    void closeOrder_shouldCloseWorkInProgressOrder() {
        int orderId = 9;
        Order order = createOrder(orderId, OrderStatus.WORK_IN_PROGRESS);
        Period actualPeriod = new Period(LocalDateTime.now());
        order.setActualWorkPeriod(actualPeriod);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.closeOrder(orderId);

        assertEquals(OrderStatus.CLOSED, order.getStatus());
        assertNotNull(order.getClosedAt());
        assertNotNull(actualPeriod.getEnd());
        verify(orderRepository).save(order);
        verify(masterService).freeMaster(order.getMasterId());
        verify(garageSpotService).freeGarageSpot(order.getGarageSpotId());
    }

    @Test
    void closeOrder_shouldDoNothingIfAlreadyClosed() {
        int orderId = 11;
        Order order = createOrder(orderId, OrderStatus.CLOSED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.closeOrder(orderId);

        assertEquals(OrderStatus.CLOSED, order.getStatus());
        verify(orderRepository, never()).save(any(Order.class));
        verify(masterService, never()).freeMaster(anyInt());
        verify(garageSpotService, never()).freeGarageSpot(anyInt());
    }

    @Test
    void cancelOrder_shouldCancelWorkInProgressOrder() {
        int orderId = 12;
        Order order = createOrder(orderId, OrderStatus.WORK_IN_PROGRESS);
        Period actualPeriod = new Period(LocalDateTime.now());
        order.setActualWorkPeriod(actualPeriod);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.cancelOrder(orderId);

        assertEquals(OrderStatus.CANCELED, order.getStatus());
        assertNotNull(order.getCanceledAt());
        assertNotNull(actualPeriod.getEnd());
        verify(orderRepository).save(order);
        verify(masterService).freeMaster(order.getMasterId());
        verify(garageSpotService).freeGarageSpot(order.getGarageSpotId());
    }

    @Test
    void cancelOrder_shouldDoNothingIfAlreadyCanceled() {
        int orderId = 13;
        Order order = createOrder(orderId, OrderStatus.CANCELED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.cancelOrder(orderId);

        assertEquals(OrderStatus.CANCELED, order.getStatus());
        verify(orderRepository, never()).save(any(Order.class));
        verify(masterService, never()).freeMaster(anyInt());
        verify(garageSpotService, never()).freeGarageSpot(anyInt());
    }

    @Test
    void shiftEstimatedWorkPeriodInCreatedOrders_shouldShiftAllCreatedOrders() {
        when(appConfig.isOrderShiftableEstimatedWorkPeriod()).thenReturn(true);
        Duration duration = Duration.ofHours(2);
        Order order1 = createOrder(1, OrderStatus.CREATED);
        Order order2 = createOrder(2, OrderStatus.CREATED);
        when(orderRepository.findFilteredAndSorted(any(), isNull())).thenReturn(List.of(order1, order2));

        orderService.shiftEstimatedWorkPeriodInCreatedOrders(duration);

        verify(orderRepository).save(List.of(order1, order2));
    }

    @Test
    void shiftEstimatedWorkPeriodInCreatedOrders_shouldThrowWhenConfigDisabled() {
        when(appConfig.isOrderShiftableEstimatedWorkPeriod()).thenReturn(false);
        Duration duration = Duration.ofHours(1);

        OperationProhibitedException ex = assertThrows(
                OperationProhibitedException.class,
                () -> orderService.shiftEstimatedWorkPeriodInCreatedOrders(duration)
        );
        assertEquals(OperationProhibitedMessages.ORDER_SHIFTING_ESTIMATED_WORK_PERIOD, ex.getMessage());
        verify(orderRepository, never()).findFilteredAndSorted(any(), any());
    }

    @Test
    void create_shouldCreateOrderAndReturnDtoWhenMasterAndGarageSpotExist() {
        CreateOrderDto dto = new CreateOrderDto();
        dto.setMasterId(100);
        dto.setGarageSpotId(200);
        dto.setPrice(500);
        dto.setEstimatedWorkPeriodStart(LocalDateTime.now().plusDays(1));
        dto.setEstimatedWorkPeriodEnd(LocalDateTime.now().plusDays(2));
        Master master = new Master("John", "Doe");
        master.setId(dto.getMasterId());
        GarageSpot spot = new GarageSpot(10);
        spot.setId(dto.getGarageSpotId());
        when(masterService.findById(dto.getMasterId())).thenReturn(Optional.of(master));
        when(garageSpotService.findById(dto.getGarageSpotId())).thenReturn(Optional.of(spot));
        Order savedOrder = new Order(dto.getPrice(), master, spot,
                new Period(dto.getEstimatedWorkPeriodStart(), dto.getEstimatedWorkPeriodEnd()));
        savedOrder.setId(500);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderDto result = orderService.create(dto);

        assertNotNull(result);
        assertEquals(500, result.getId());
        assertEquals(dto.getPrice(), result.getPrice());
        assertEquals(dto.getMasterId(), result.getMasterId());
        assertEquals(dto.getGarageSpotId(), result.getGarageSpotId());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void create_shouldThrowEntityNotFoundExceptionWhenMasterNotFound() {
        CreateOrderDto dto = new CreateOrderDto();
        dto.setMasterId(999);
        dto.setGarageSpotId(200);
        when(masterService.findById(999)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.create(dto)
        );
        assertTrue(ex.getMessage().contains("Master не найден"));
        verify(garageSpotService, never()).findById(anyInt());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void create_shouldThrowEntityNotFoundExceptionWhenGarageSpotNotFound() {
        CreateOrderDto dto = new CreateOrderDto();
        dto.setMasterId(100);
        dto.setGarageSpotId(999);
        when(masterService.findById(100)).thenReturn(Optional.of(new Master("a", "b")));
        when(garageSpotService.findById(999)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> orderService.create(dto)
        );
        assertTrue(ex.getMessage().contains("GarageSpot не найден"));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void findAll_shouldConvertEachEntityToDto() {
        FilterParams filterParams = FilterParams.builder().statuses(OrderStatus.CREATED).build();
        SortParams sortParams = new SortParams(SortCriteria.ID, SortDirection.ASC);
        Order order1 = createOrder(1, OrderStatus.CREATED);
        Order order2 = createOrder(2, OrderStatus.CREATED);
        when(orderRepository.findFilteredAndSorted(filterParams, sortParams)).thenReturn(List.of(order1, order2));

        List<OrderDto> result = orderService.findAll(filterParams, sortParams);

        assertEquals(2, result.size());
        assertEquals(order1.getId(), result.get(0).getId());
        assertEquals(order2.getId(), result.get(1).getId());
    }

    @Test
    void findAll_shouldReturnEmptyListWhenRepositoryReturnsEmpty() {
        FilterParams filterParams = FilterParams.builder().build();
        when(orderRepository.findFilteredAndSorted(filterParams, null)).thenReturn(List.of());

        List<OrderDto> result = orderService.findAll(filterParams, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void start_shouldCallStartWorkingAndReturnDto() {
        int orderId = 20;
        Order order = createOrder(orderId, OrderStatus.WORK_IN_PROGRESS);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderService).startWorking(orderId);

        OrderDto result = orderService.start(orderId);

        verify(orderService).startWorking(orderId);
        assertNotNull(result);
        assertEquals(orderId, result.getId());
    }

    @Test
    void close_shouldCallCloseOrderAndReturnDto() {
        int orderId = 21;
        Order order = createOrder(orderId, OrderStatus.CLOSED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderService).closeOrder(orderId);

        OrderDto result = orderService.close(orderId);

        verify(orderService).closeOrder(orderId);
        assertEquals(orderId, result.getId());
    }

    @Test
    void cancel_shouldCallCancelOrderAndReturnDto() {
        int orderId = 22;
        Order order = createOrder(orderId, OrderStatus.CANCELED);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        doNothing().when(orderService).cancelOrder(orderId);

        OrderDto result = orderService.cancel(orderId);

        verify(orderService).cancelOrder(orderId);
        assertEquals(orderId, result.getId());
    }

    private Order createOrder(int id, OrderStatus status) {
        Master master = new Master("test", "master");
        master.setId(100);
        GarageSpot spot = new GarageSpot(10);
        spot.setId(200);
        Order order = new Order(1000, master, spot, new Period(LocalDateTime.now(), LocalDateTime.now().plusHours(1)));
        order.setId(id);
        order.setStatus(status);
        return order;
    }
}