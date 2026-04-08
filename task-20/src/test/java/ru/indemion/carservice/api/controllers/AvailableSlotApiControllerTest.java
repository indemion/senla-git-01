package ru.indemion.carservice.api.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.indemion.carservice.dto.AvailableSlotsCountAtDateResponse;
import ru.indemion.carservice.models.services.AvailabilityService;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailableSlotApiControllerTest {

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private AvailableSlotApiController controller;

    @Test
    void getAvailableSlotsCountAtDate_shouldCallServiceAndReturnResponseWithCount() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 4, 5);
        int expectedCount = 7;
        when(availabilityService.countAvailableSlotsAtDate(date)).thenReturn(expectedCount);

        // Act
        AvailableSlotsCountAtDateResponse response = controller.getAvailableSlotsCountAtDate(date);

        // Assert
        assertNotNull(response);
        assertEquals(expectedCount, response.getCount());
        verify(availabilityService).countAvailableSlotsAtDate(date);
        verifyNoMoreInteractions(availabilityService);
    }

    @Test
    void getAvailableSlotsCountAtDate_shouldPropagateExceptionFromService() {
        // Arrange
        LocalDate date = LocalDate.of(2025, 4, 5);
        RuntimeException expectedException = new RuntimeException("Service error");
        when(availabilityService.countAvailableSlotsAtDate(date)).thenThrow(expectedException);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> controller.getAvailableSlotsCountAtDate(date));
        assertSame(expectedException, thrown);
        verify(availabilityService).countAvailableSlotsAtDate(date);
    }
}