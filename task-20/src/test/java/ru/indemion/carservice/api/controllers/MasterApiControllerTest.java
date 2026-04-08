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
import ru.indemion.carservice.dto.MasterDto;
import ru.indemion.carservice.dto.SaveMasterDto;
import ru.indemion.carservice.models.master.MasterService;
import ru.indemion.carservice.models.master.SortCriteria;
import ru.indemion.carservice.models.master.SortParams;
import ru.indemion.carservice.util.Util;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MasterApiControllerTest {

    @Mock
    private MasterService masterService;

    @InjectMocks
    private MasterApiController controller;

    // ==================== findAll ====================
    @Test
    void findAll_shouldCallServiceWithDefaultSortParams() {
        List<MasterDto> expectedList = List.of(new MasterDto());
        when(masterService.findAll(any(SortParams.class))).thenReturn(expectedList);

        List<MasterDto> result = controller.findAll(SortCriteria.ID, SortDirection.ASC);

        assertSame(expectedList, result);
        verify(masterService).findAll(argThat(params ->
                params.getSortCriteria() == SortCriteria.ID &&
                        params.getSortDirection() == SortDirection.ASC
        ));
    }

    @Test
    void findAll_shouldPassCustomSortParams() {
        when(masterService.findAll(any())).thenReturn(List.of());

        controller.findAll(SortCriteria.FULLNAME, SortDirection.DESC);

        verify(masterService).findAll(argThat(params ->
                params.getSortCriteria() == SortCriteria.FULLNAME &&
                        params.getSortDirection() == SortDirection.DESC
        ));
    }

    @Test
    void findAll_shouldPropagateException() {
        when(masterService.findAll(any())).thenThrow(new RuntimeException("Service error"));
        assertThrows(RuntimeException.class, () -> controller.findAll(SortCriteria.ID, SortDirection.ASC));
    }

    // ==================== findById ====================
    @Test
    void findById_shouldReturnDtoFromService() {
        int id = 10;
        MasterDto expectedDto = new MasterDto(id, "John", "Doe", "FREE", null);
        when(masterService.find(id)).thenReturn(expectedDto);

        MasterDto result = controller.findById(id);

        assertSame(expectedDto, result);
        verify(masterService).find(id);
    }

    @Test
    void findById_shouldPropagateException() {
        int id = 99;
        when(masterService.find(id)).thenThrow(new RuntimeException("Not found"));
        assertThrows(RuntimeException.class, () -> controller.findById(id));
    }

    // ==================== create ====================
    @Test
    void create_shouldReturnCreatedDto() {
        SaveMasterDto dto = new SaveMasterDto("Anna", "Smith");
        MasterDto expectedDto = new MasterDto(1, "Anna", "Smith", "FREE", null);
        when(masterService.create(dto)).thenReturn(expectedDto);

        MasterDto result = controller.create(dto);

        assertSame(expectedDto, result);
        verify(masterService).create(dto);
    }

    @Test
    void create_shouldPropagateException() {
        SaveMasterDto dto = new SaveMasterDto("Anna", "Smith");
        when(masterService.create(dto)).thenThrow(new RuntimeException("Creation failed"));
        assertThrows(RuntimeException.class, () -> controller.create(dto));
    }

    // ==================== update ====================
    @Test
    void update_shouldCallServiceWithIdAndDto() {
        int id = 5;
        SaveMasterDto dto = new SaveMasterDto("Updated", "Name");

        controller.update(id, dto);

        verify(masterService).update(id, dto);
    }

    @Test
    void update_shouldPropagateException() {
        int id = 5;
        SaveMasterDto dto = new SaveMasterDto("Updated", "Name");
        doThrow(new RuntimeException("Update failed")).when(masterService).update(id, dto);
        assertThrows(RuntimeException.class, () -> controller.update(id, dto));
    }

    // ==================== delete ====================
    @Test
    void delete_shouldCallServiceWithId() {
        int id = 7;
        doNothing().when(masterService).delete(id);
        controller.delete(id);
        verify(masterService).delete(id);
    }

    @Test
    void delete_shouldPropagateException() {
        int id = 7;
        doThrow(new RuntimeException("Delete failed")).when(masterService).delete(id);
        assertThrows(RuntimeException.class, () -> controller.delete(id));
    }

    // ==================== exportCsv ====================
    @Test
    void exportCsv_shouldReturnResponseEntityFromUtil() {
        String csvData = "id,firstname,lastname\n1,John,Doe";
        byte[] expectedBytes = csvData.getBytes();
        ResponseEntity<byte[]> expectedResponse = ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=masters.csv")
                .body(expectedBytes);
        when(masterService.getCsvData()).thenReturn(csvData);

        try (MockedStatic<Util> mockedUtil = mockStatic(Util.class)) {
            mockedUtil.when(() -> Util.getResponseEntityForCsvData(csvData, "masters"))
                    .thenReturn(expectedResponse);

            ResponseEntity<byte[]> result = controller.exportCsv();

            assertSame(expectedResponse, result);
            verify(masterService).getCsvData();
        }
    }

    @Test
    void exportCsv_shouldPropagateExceptionFromService() {
        when(masterService.getCsvData()).thenThrow(new RuntimeException("Export error"));
        assertThrows(RuntimeException.class, () -> controller.exportCsv());
    }

    // ==================== importCsv ====================
    @Test
    void importCsv_shouldCallServiceWithMultipartFile() {
        MultipartFile file = new MockMultipartFile("file", "masters.csv", "text/csv", "data".getBytes());
        doNothing().when(masterService).importCsv(file);
        controller.importCsv(file);
        verify(masterService).importCsv(file);
    }

    @Test
    void importCsv_shouldPropagateException() {
        MultipartFile file = new MockMultipartFile("file", "masters.csv", "text/csv", "data".getBytes());
        doThrow(new RuntimeException("Import failed")).when(masterService).importCsv(file);
        assertThrows(RuntimeException.class, () -> controller.importCsv(file));
    }
}