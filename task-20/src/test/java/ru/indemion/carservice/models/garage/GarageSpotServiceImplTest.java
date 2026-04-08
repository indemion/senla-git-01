package ru.indemion.carservice.models.garage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.indemion.carservice.common.OperationProhibitedMessages;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.common.SortDirection;
import ru.indemion.carservice.common.SortParams;
import ru.indemion.carservice.config.AppConfig;
import ru.indemion.carservice.dto.CreateGarageSpotDto;
import ru.indemion.carservice.dto.GarageSpotDto;
import ru.indemion.carservice.exceptions.OperationProhibitedException;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.repositories.GarageSpotRepository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class GarageSpotServiceImplTest {
    @Mock
    private AppConfig appConfig;

    @Mock
    private GarageSpotRepository garageSpotRepository;

    @InjectMocks
    private GarageSpotServiceImpl garageSpotService;

    @Test
    void createOrGet_shouldReturnExistingGarageSpot() {
        // ARRANGE
        int number = 101;
        GarageSpot existingSpot = new GarageSpot(number);
        existingSpot.setId(1);
        when(garageSpotRepository.findByNumber(number)).thenReturn(Optional.of(existingSpot));

        // ACT
        GarageSpot result = garageSpotService.createOrGet(number);

        // ASSERT
        assertEquals(existingSpot, result);
        verify(garageSpotRepository, never()).save(any(GarageSpot.class));
    }

    @Test
    void createOrGet_shouldCreateAndReturnNewGarageSpot() {
        // ARRANGE
        int number = 102;
        GarageSpot newSpot = new GarageSpot(number);
        when(garageSpotRepository.findByNumber(number)).thenReturn(Optional.empty());
        when(garageSpotRepository.save(any(GarageSpot.class))).thenReturn(newSpot);

        // ACT
        GarageSpot result = garageSpotService.createOrGet(number);

        // ASSERT
        assertEquals(newSpot, result);
        verify(garageSpotRepository).save(any(GarageSpot.class));
    }

    @Test
    void delete_shouldThrownOperationProhibitedExceptionWhenRemovalDisabled() {
        // ARRANGE
        int id = 1;
        when(appConfig.isGarageSpotRemovable()).thenReturn(false);

        // ACT & ASSERT
        OperationProhibitedException exception = assertThrows(OperationProhibitedException.class,
                () -> garageSpotService.delete(id));

        // ASSERT
        assertEquals(OperationProhibitedMessages.GARAGE_SPOT_REMOVING, exception.getMessage());
        verify(garageSpotRepository, never()).delete(any());
    }

    @Test
    void delete_shouldDeleteSpotWhenRemovalEnabledAndSpotExists() {
        // ARRANGE
        int number = 101;
        GarageSpot existingSpot = new GarageSpot(number);
        existingSpot.setId(1);
        when(appConfig.isGarageSpotRemovable()).thenReturn(true);
        when(garageSpotRepository.findByNumber(number)).thenReturn(Optional.of(existingSpot));

        // ACT
        garageSpotService.delete(number);

        // ASSERT
        verify(garageSpotRepository).delete(existingSpot);
    }

    @Test
    void delete_shouldDoNothingWhenRemovalEnabledAndSpotNotExists() {
        // ARRANGE
        int number = 101;
        when(appConfig.isGarageSpotRemovable()).thenReturn(true);
        when(garageSpotRepository.findByNumber(number)).thenReturn(Optional.empty());

        // ACT
        garageSpotService.delete(number);

        // ASSERT
        verify(garageSpotRepository, never()).delete(any());
    }

    @Test
    void save_shouldSaveGarageSpot() {
        // ARRANGE
        GarageSpot newGarageSpot = new GarageSpot(101);
        when(garageSpotRepository.save(newGarageSpot)).thenReturn(newGarageSpot);

        // ACT
        garageSpotService.save(newGarageSpot);

        // ASSERT
        verify(garageSpotRepository).save(newGarageSpot);
    }

    @Test
    void freeGarageSpot_shouldSetOrderAtWorkToNullWhenSpotExists() {
        // ARRANGE
        int id = 1;
        GarageSpot existingSpot = new GarageSpot();
        existingSpot.setId(id);
        existingSpot.setOrderAtWork(mock(Order.class));
        when(garageSpotRepository.findById(id)).thenReturn(Optional.of(existingSpot));

        // ACT
        garageSpotService.freeGarageSpot(id);

        // ASSERT
        assertNull(existingSpot.getOrderAtWork());
        verify(garageSpotRepository).save(existingSpot);
    }

    @Test
    void freeGarageSpot_shouldDoNothingWhenSpotNotExists() {
        // ARRANGE
        int id = 1;
        when(garageSpotRepository.findById(id)).thenReturn(Optional.empty());

        // ACT
        garageSpotService.freeGarageSpot(id);

        // ASSERT
        verify(garageSpotRepository, never()).save(any(GarageSpot.class));
    }

    @Test
    void getFreeGarageSpotInPeriod_shouldReturnFreeGarageSpotInPeriod() {
        // ARRANGE
        Period period = mock(Period.class);
        when(garageSpotRepository.findFreeGarageSpotsInPeriod(period))
                .thenReturn(List.of(mock(GarageSpot.class)));

        // ACT
        List<GarageSpot> garageSpotList = garageSpotService.getFreeGarageSpotsInPeriod(period);

        // ARRANGE
        assertFalse(garageSpotList.isEmpty());
        verify(garageSpotRepository).findFreeGarageSpotsInPeriod(period);
    }

    @Test
    void getFreeGarageSpotInPeriod_shouldReturnEmptyListOfGarageSpotsInPeriod() {
        // ARRANGE
        Period period = mock(Period.class);
        when(garageSpotRepository.findFreeGarageSpotsInPeriod(period))
                .thenReturn(new LinkedList<>());

        // ACT
        List<GarageSpot> garageSpotList = garageSpotService.getFreeGarageSpotsInPeriod(period);

        // ARRANGE
        assertTrue(garageSpotList.isEmpty());
        verify(garageSpotRepository).findFreeGarageSpotsInPeriod(period);
    }

    @Test
    void findById_shouldReturnExistingGarageSpot() {
        // ARRANGE
        int id = 1;
        GarageSpot existingGarageSpot = new GarageSpot(101);
        existingGarageSpot.setId(id);
        when(garageSpotRepository.findById(id)).thenReturn(Optional.of(existingGarageSpot));

        // ACT
        Optional<GarageSpot> optionalGarageSpot = garageSpotService.findById(id);

        // ASSERT
        assertTrue(optionalGarageSpot.isPresent());
        assertEquals(id, optionalGarageSpot.get().getId());
        verify(garageSpotRepository).findById(id);
    }

    @Test
    void findByNumber_shouldReturnExistingGarageSpot() {
        // ARRANGE
        int number = 101;
        GarageSpot existingGarageSpot = new GarageSpot(number);
        when(garageSpotRepository.findByNumber(number)).thenReturn(Optional.of(existingGarageSpot));

        // ACT
        Optional<GarageSpot> optionalGarageSpot = garageSpotService.findByNumber(number);

        // ASSERT
        assertTrue(optionalGarageSpot.isPresent());
        assertEquals(number, optionalGarageSpot.get().getNumber());
        verify(garageSpotRepository).findByNumber(number);
    }

    @Test
    void findAll_shouldReturnListOfDtos() {
        // ARRANGE
        FilterParams filterParams = new FilterParams(GarageSpotStatus.FREE);
        SortParams<SortCriteria> sortParams = new SortParams<>(SortCriteria.NUMBER, SortDirection.ASC);
        GarageSpot spot1 = new GarageSpot(1);
        GarageSpot spot2 = new GarageSpot(2);
        when(garageSpotRepository.findFilteredAndSorted(filterParams, sortParams)).thenReturn(List.of(spot1, spot2));

        // ACT
        List<GarageSpotDto> result = garageSpotService.findAll(filterParams, sortParams);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        GarageSpotDto dto1 = result.get(0);
        assertEquals(1, dto1.getNumber());
        assertEquals("free", dto1.getStatus());

        GarageSpotDto dto2 = result.get(1);
        assertEquals(2, dto2.getNumber());
        assertEquals("free", dto2.getStatus());

        verify(garageSpotRepository).findFilteredAndSorted(filterParams, sortParams);
    }

    @Test
    void findAll_shouldReturnEmptyListWhenRepositoryReturnsEmpty() {
        // ARRANGE
        when(garageSpotRepository.findFilteredAndSorted(null, null))
                .thenReturn(List.of());

        // ACT
        List<GarageSpotDto> result = garageSpotService.findAll(null, null);

        // ASSERT
        assertTrue(result.isEmpty());
    }

    @Test
    void createOrGet_fromDto_shouldReturnExistingDtoWhenSpotExists() {
        // ARRANGE
        int number = 101;
        CreateGarageSpotDto dto = new CreateGarageSpotDto();
        dto.setNumber(number);
        int id = 1;
        GarageSpot existingSpot = new GarageSpot(number);
        existingSpot.setId(id);
        when(garageSpotRepository.findByNumber(number)).thenReturn(Optional.of(existingSpot));

        // ACT
        GarageSpotDto result = garageSpotService.createOrGet(dto);

        // ASSERT
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(number, result.getNumber());
        verify(garageSpotRepository, never()).save(any(GarageSpot.class));
    }

    @Test
    void createOrGet_fromDto_shouldCreateNewSpotAndConvertToDto() {
        // ARRANGE
        int number = 101;
        CreateGarageSpotDto dto = new CreateGarageSpotDto();
        dto.setNumber(number);
        int id = 1;
        GarageSpot newSpot = new GarageSpot(number);
        newSpot.setId(id);
        when(garageSpotRepository.findByNumber(number)).thenReturn(Optional.empty());
        when(garageSpotRepository.save(any(GarageSpot.class))).thenReturn(newSpot);

        // ACT
        GarageSpotDto result = garageSpotService.createOrGet(dto);

        // ASSERT
        assertEquals(id, result.getId());
        assertEquals(number, result.getNumber());
        verify(garageSpotRepository).save(any(GarageSpot.class));
    }
}