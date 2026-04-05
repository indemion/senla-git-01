package ru.indemion.carservice.models.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.MasterService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceTest {
    @Mock
    private GarageSpotService garageSpotService;

    @Mock
    private MasterService masterService;

    @InjectMocks
    private AvailabilityService availabilityService;

    @Test
    void countAvailableSlotsAtDate_shouldReturnMinWhenSpotsLessThanMasters() {
        LocalDate date = LocalDate.of(2025, 4, 10);
        LocalDateTime expectedStart = date.atStartOfDay();
        LocalDateTime expectedEnd = date.atTime(LocalTime.MAX);

        when(garageSpotService.getFreeGarageSpotsInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(generateGarageSpotList(2)); // 2 места

        when(masterService.getMastersFreeInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(generateMasterList(3)); // 3 мастера

        int result = availabilityService.countAvailableSlotsAtDate(date);

        assertEquals(2, result);
    }

    @Test
    void countAvailableSlotsAtDate_shouldReturnMinWhenMastersLessThanSpots() {
        LocalDate date = LocalDate.now();
        LocalDateTime expectedStart = date.atStartOfDay();
        LocalDateTime expectedEnd = date.atTime(LocalTime.MAX);

        when(garageSpotService.getFreeGarageSpotsInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(generateGarageSpotList(4)); // 4 места

        when(masterService.getMastersFreeInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(generateMasterList(1)); // 1 мастер

        int result = availabilityService.countAvailableSlotsAtDate(date);

        assertEquals(1, result);
    }

    @Test
    void countAvailableSlotsAtDate_shouldReturnZeroWhenNoFreeSpots() {
        LocalDate date = LocalDate.now();
        LocalDateTime expectedStart = date.atStartOfDay();
        LocalDateTime expectedEnd = date.atTime(LocalTime.MAX);

        when(garageSpotService.getFreeGarageSpotsInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(Collections.emptyList());

        when(masterService.getMastersFreeInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(generateMasterList(2));

        int result = availabilityService.countAvailableSlotsAtDate(date);

        assertEquals(0, result);
    }

    @Test
    void countAvailableSlotsAtDate_shouldReturnZeroWhenNoFreeMasters() {
        LocalDate date = LocalDate.now();
        LocalDateTime expectedStart = date.atStartOfDay();
        LocalDateTime expectedEnd = date.atTime(LocalTime.MAX);

        when(garageSpotService.getFreeGarageSpotsInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(generateGarageSpotList(2));

        when(masterService.getMastersFreeInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(Collections.emptyList());

        int result = availabilityService.countAvailableSlotsAtDate(date);

        assertEquals(0, result);
    }

    @Test
    void countAvailableSlotsAtDate_shouldReturnZeroWhenBothListsEmpty() {
        LocalDate date = LocalDate.now();
        LocalDateTime expectedStart = date.atStartOfDay();
        LocalDateTime expectedEnd = date.atTime(LocalTime.MAX);

        when(garageSpotService.getFreeGarageSpotsInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(Collections.emptyList());

        when(masterService.getMastersFreeInPeriod(argThat(period ->
                period.getStart().equals(expectedStart) && period.getEnd().equals(expectedEnd)
        ))).thenReturn(Collections.emptyList());

        int result = availabilityService.countAvailableSlotsAtDate(date);

        assertEquals(0, result);
    }

    private List<GarageSpot> generateGarageSpotList(int numberOfGarageSpots) {
        List<GarageSpot> garageSpotList = new LinkedList<>();
        for (int i = 0; i < numberOfGarageSpots; i++) {
            garageSpotList.add(new GarageSpot(i+1));
        }
        return garageSpotList;
    }

    private List<Master> generateMasterList(int numberOfMasters) {
        List<Master> masterList = new LinkedList<>();
        for (int i = 0; i < numberOfMasters; i++) {
            masterList.add(new Master("A" + i, "B" + i));
        }
        return masterList;
    }
}