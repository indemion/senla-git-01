package ru.indemion.carservice.api.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.SortDirection;
import ru.indemion.carservice.common.SortParams;
import ru.indemion.carservice.dto.CreateGarageSpotDto;
import ru.indemion.carservice.dto.GarageSpotDto;
import ru.indemion.carservice.models.garage.FilterParams;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.garage.GarageSpotStatus;
import ru.indemion.carservice.models.garage.SortCriteria;
import ru.indemion.carservice.util.Util;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageSpotApiControllerTest {

    @Mock
    private GarageSpotService garageSpotService;

    @InjectMocks
    private GarageSpotApiController controller;

    // ==================== findAll ====================
    @Test
    void findAll_shouldCallServiceWithDefaultParams() {
        // Arrange
        List<GarageSpotDto> expectedList = List.of(new GarageSpotDto());
        when(garageSpotService.findAll(any(FilterParams.class), any(SortParams.class)))
                .thenReturn(expectedList);

        // Act
        List<GarageSpotDto> result = controller.findAll(null, SortCriteria.ID, SortDirection.ASC);

        // Assert
        assertSame(expectedList, result);
        verify(garageSpotService).findAll(
                argThat(params -> params.status() == null),
                argThat(sort -> sort.getSortCriteria() == SortCriteria.ID
                        && sort.getSortDirection() == SortDirection.ASC)
        );
    }

    @Test
    void findAll_shouldPassStatusAndSortingParams() {
        // Arrange
        List<GarageSpotDto> expectedList = List.of();
        when(garageSpotService.findAll(any(FilterParams.class), any(SortParams.class)))
                .thenReturn(expectedList);

        // Act
        controller.findAll(GarageSpotStatus.FREE, SortCriteria.NUMBER, SortDirection.DESC);

        // Assert
        verify(garageSpotService).findAll(
                argThat(params -> params.status() == GarageSpotStatus.FREE),
                argThat(sort -> sort.getSortCriteria() == SortCriteria.NUMBER
                        && sort.getSortDirection() == SortDirection.DESC)
        );
    }

    @Test
    void findAll_shouldPropagateExceptionFromService() {
        // Arrange
        when(garageSpotService.findAll(any(), any())).thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.findAll(null, SortCriteria.ID, SortDirection.ASC));
    }

    // ==================== create ====================
    @Test
    void create_shouldReturnCreatedDto() {
        // Arrange
        CreateGarageSpotDto dto = new CreateGarageSpotDto(123);
        GarageSpotDto expectedDto = new GarageSpotDto(1, 123, "FREE", null);
        when(garageSpotService.createOrGet(dto)).thenReturn(expectedDto);

        // Act
        GarageSpotDto result = controller.create(dto);

        // Assert
        assertSame(expectedDto, result);
        verify(garageSpotService).createOrGet(dto);
    }

    @Test
    void create_shouldPropagateExceptionFromService() {
        // Arrange
        CreateGarageSpotDto dto = new CreateGarageSpotDto(123);
        when(garageSpotService.createOrGet(dto)).thenThrow(new RuntimeException("Creation failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.create(dto));
    }

    // ==================== delete ====================
    @Test
    void delete_shouldCallServiceWithId() {
        // Arrange
        int id = 5;
        doNothing().when(garageSpotService).delete(id);

        // Act
        controller.delete(id);

        // Assert
        verify(garageSpotService).delete(id);
    }

    @Test
    void delete_shouldPropagateExceptionFromService() {
        // Arrange
        int id = 5;
        doThrow(new RuntimeException("Delete failed")).when(garageSpotService).delete(id);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.delete(id));
    }

    // ==================== exportCsv ====================
    @Test
    void exportCsv_shouldReturnResponseEntityFromUtil() {
        // Arrange
        String csvData = "id,name\n1,Spot1";
        byte[] expectedBytes = csvData.getBytes();
        ResponseEntity<byte[]> expectedResponse = ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=garage-spots.csv")
                .body(expectedBytes);
        when(garageSpotService.getCsvData()).thenReturn(csvData);

        try (MockedStatic<Util> mockedUtil = mockStatic(Util.class)) {
            mockedUtil.when(() -> Util.getResponseEntityForCsvData(csvData, "garage-spots"))
                    .thenReturn(expectedResponse);

            // Act
            ResponseEntity<byte[]> result = controller.exportCsv();

            // Assert
            assertSame(expectedResponse, result);
            verify(garageSpotService).getCsvData();
        }
    }

    @Test
    void exportCsv_shouldPropagateExceptionFromService() {
        // Arrange
        when(garageSpotService.getCsvData()).thenThrow(new RuntimeException("Export failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.exportCsv());
    }

    // ==================== importCsv ====================
    @Test
    void importCsv_shouldCallServiceWithMultipartFile() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());
        doNothing().when(garageSpotService).importCsv(file);

        // Act
        controller.importCsv(file);

        // Assert
        verify(garageSpotService).importCsv(file);
    }

    @Test
    void importCsv_shouldPropagateExceptionFromService() {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "data".getBytes());
        doThrow(new RuntimeException("Import failed")).when(garageSpotService).importCsv(file);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> controller.importCsv(file));
    }
}