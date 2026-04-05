package ru.indemion.carservice.api.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.common.SortDirection;
import ru.indemion.carservice.dto.CreateOrderDto;
import ru.indemion.carservice.dto.OrderDto;
import ru.indemion.carservice.models.order.*;
import ru.indemion.carservice.util.Util;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderApiControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderApiController controller;

    // ==================== findAll ====================
    @Test
    void findAll_shouldCallServiceWithDefaultParams() {
        // Arrange
        List<OrderDto> expected = List.of(new OrderDto());
        when(orderService.findAll(any(FilterParams.class), any(SortParams.class)))
                .thenReturn(expected);

        // Act
        List<OrderDto> result = controller.findAll(null, null, null, SortCriteria.ID, SortDirection.ASC);

        // Assert
        assertSame(expected, result);

        ArgumentCaptor<FilterParams> filterCaptor = ArgumentCaptor.forClass(FilterParams.class);
        ArgumentCaptor<SortParams> sortCaptor = ArgumentCaptor.forClass(SortParams.class);
        verify(orderService).findAll(filterCaptor.capture(), sortCaptor.capture());

        FilterParams capturedFilter = filterCaptor.getValue();
        assertTrue(capturedFilter.getStatuses().isEmpty());
        assertNull(capturedFilter.getMasterId());
        assertNull(capturedFilter.getEstimatedWorkStartInPeriod());

        SortParams capturedSort = sortCaptor.getValue();
        assertEquals(SortCriteria.ID, capturedSort.getSortCriteria());
        assertEquals(SortDirection.ASC, capturedSort.getSortDirection());
    }

    @Test
    void findAll_shouldParseStatusesParameter() {
        // Arrange
        String statuses = "CREATED,WORK_IN_PROGRESS";
        when(orderService.findAll(any(), any())).thenReturn(List.of());

        // Act
        controller.findAll(statuses, null, null, SortCriteria.ID, SortDirection.ASC);

        // Assert
        ArgumentCaptor<FilterParams> filterCaptor = ArgumentCaptor.forClass(FilterParams.class);
        verify(orderService).findAll(filterCaptor.capture(), any());
        FilterParams params = filterCaptor.getValue();
        assertNotNull(params.getStatuses());
        assertEquals(2, params.getStatuses().size());
        assertTrue(params.getStatuses().contains(OrderStatus.CREATED));
        assertTrue(params.getStatuses().contains(OrderStatus.WORK_IN_PROGRESS));
    }

    @Test
    void findAll_shouldParseMasterIdParameter() {
        // Arrange
        Integer masterId = 10;
        when(orderService.findAll(any(), any())).thenReturn(List.of());

        // Act
        controller.findAll(null, masterId, null, SortCriteria.ID, SortDirection.ASC);

        // Assert
        ArgumentCaptor<FilterParams> filterCaptor = ArgumentCaptor.forClass(FilterParams.class);
        verify(orderService).findAll(filterCaptor.capture(), any());
        assertEquals(masterId, filterCaptor.getValue().getMasterId());
    }

    @Test
    void findAll_shouldParsePeriodParameter() {
        // Arrange
        String period = "2025-04-10T09:00:00,2025-04-10T17:00:00";
        when(orderService.findAll(any(), any())).thenReturn(List.of());

        // Act
        controller.findAll(null, null, period, SortCriteria.ID, SortDirection.ASC);

        // Assert
        ArgumentCaptor<FilterParams> filterCaptor = ArgumentCaptor.forClass(FilterParams.class);
        verify(orderService).findAll(filterCaptor.capture(), any());
        Period capturedPeriod = filterCaptor.getValue().getEstimatedWorkStartInPeriod();
        assertNotNull(capturedPeriod);
        assertEquals(LocalDateTime.parse("2025-04-10T09:00:00"), capturedPeriod.getStart());
        assertEquals(LocalDateTime.parse("2025-04-10T17:00:00"), capturedPeriod.getEnd());
    }

    @Test
    void findAll_shouldThrowExceptionWhenPeriodHasInvalidFormat() {
        // Arrange
        String invalidPeriod = "2025-04-10T09:00:00"; // only one part

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> controller.findAll(null, null, invalidPeriod, SortCriteria.ID, SortDirection.ASC));
        verify(orderService, never()).findAll(any(), any());
    }

    @Test
    void findAll_shouldPropagateServiceException() {
        when(orderService.findAll(any(), any())).thenThrow(new RuntimeException("Service error"));
        assertThrows(RuntimeException.class,
                () -> controller.findAll(null, null, null, SortCriteria.ID, SortDirection.ASC));
    }

    // ==================== create ====================
    @Test
    void create_shouldCallServiceAndReturnDto() {
        CreateOrderDto dto = new CreateOrderDto();
        OrderDto expected = new OrderDto();
        when(orderService.create(dto)).thenReturn(expected);

        OrderDto result = controller.create(dto);

        assertSame(expected, result);
        verify(orderService).create(dto);
    }

    @Test
    void create_shouldPropagateException() {
        CreateOrderDto dto = new CreateOrderDto();
        when(orderService.create(dto)).thenThrow(new RuntimeException("Creation failed"));
        assertThrows(RuntimeException.class, () -> controller.create(dto));
    }

    // ==================== delete ====================
    @Test
    void delete_shouldCallServiceWithId() {
        int id = 5;
        doNothing().when(orderService).delete(id);
        controller.delete(id);
        verify(orderService).delete(id);
    }

    @Test
    void delete_shouldPropagateException() {
        int id = 5;
        doThrow(new RuntimeException("Delete failed")).when(orderService).delete(id);
        assertThrows(RuntimeException.class, () -> controller.delete(id));
    }

    // ==================== startWorking ====================
    @Test
    void startWorking_shouldCallServiceStartAndReturnDto() {
        int id = 1;
        OrderDto expected = new OrderDto();
        when(orderService.start(id)).thenReturn(expected);
        OrderDto result = controller.startWorking(id);
        assertSame(expected, result);
        verify(orderService).start(id);
    }

    // ==================== close ====================
    @Test
    void close_shouldCallServiceCloseAndReturnDto() {
        int id = 2;
        OrderDto expected = new OrderDto();
        when(orderService.close(id)).thenReturn(expected);
        OrderDto result = controller.close(id);
        assertSame(expected, result);
        verify(orderService).close(id);
    }

    // ==================== cancel ====================
    @Test
    void cancel_shouldCallServiceCancelAndReturnDto() {
        int id = 3;
        OrderDto expected = new OrderDto();
        when(orderService.cancel(id)).thenReturn(expected);
        OrderDto result = controller.cancel(id);
        assertSame(expected, result);
        verify(orderService).cancel(id);
    }

    // ==================== shiftOrdersEstimatedWorkPeriod ====================
    @Test
    void shiftOrdersEstimatedWorkPeriod_shouldCallService() {
        int duration = 5;
        doNothing().when(orderService).shiftEstimatedWorkPeriod(duration);
        controller.shiftOrdersEstimatedWorkPeriod(duration);
        verify(orderService).shiftEstimatedWorkPeriod(duration);
    }

    @Test
    void shiftOrdersEstimatedWorkPeriod_shouldPropagateException() {
        int duration = 5;
        doThrow(new RuntimeException("Shift failed")).when(orderService).shiftEstimatedWorkPeriod(duration);
        assertThrows(RuntimeException.class, () -> controller.shiftOrdersEstimatedWorkPeriod(duration));
    }

    // ==================== exportCsv ====================
    @Test
    void exportCsv_shouldReturnResponseEntityFromUtil() {
        String csvData = "id,price\n1,1000";
        byte[] expectedBytes = csvData.getBytes();
        ResponseEntity<byte[]> expectedResponse = ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=orders.csv")
                .body(expectedBytes);
        when(orderService.getCsvData()).thenReturn(csvData);

        try (MockedStatic<Util> mockedUtil = mockStatic(Util.class)) {
            mockedUtil.when(() -> Util.getResponseEntityForCsvData(csvData, "orders"))
                    .thenReturn(expectedResponse);

            ResponseEntity<byte[]> result = controller.exportCsv();

            assertSame(expectedResponse, result);
            verify(orderService).getCsvData();
        }
    }

    @Test
    void exportCsv_shouldPropagateExceptionFromService() {
        when(orderService.getCsvData()).thenThrow(new RuntimeException("Export error"));
        assertThrows(RuntimeException.class, () -> controller.exportCsv());
    }

    // ==================== importCsv ====================
    @Test
    void importCsv_shouldCallServiceWithMultipartFile() {
        MultipartFile file = new MockMultipartFile("file", "orders.csv", "text/csv", "data".getBytes());
        doNothing().when(orderService).importCsv(file);
        controller.importCsv(file);
        verify(orderService).importCsv(file);
    }

    @Test
    void importCsv_shouldPropagateException() {
        MultipartFile file = new MockMultipartFile("file", "orders.csv", "text/csv", "data".getBytes());
        doThrow(new RuntimeException("Import failed")).when(orderService).importCsv(file);
        assertThrows(RuntimeException.class, () -> controller.importCsv(file));
    }
}