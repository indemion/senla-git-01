package ru.indemion.carservice.models.master;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.indemion.carservice.common.SortDirection;
import ru.indemion.carservice.dto.MasterDto;
import ru.indemion.carservice.dto.SaveMasterDto;
import ru.indemion.carservice.exceptions.EntityNotFoundException;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.repositories.MasterRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MasterServiceImplTest {
    @Mock
    private MasterRepository masterRepository;

    @InjectMocks
    private MasterServiceImpl masterService;

    @Test
    void delete_shouldDeleteWhenMasterExists() {
        int masterId = 1;
        Master master = new Master("Иван", "Иванович");
        master.setId(masterId);
        when(masterRepository.findById(masterId)).thenReturn(Optional.of(master));

        masterService.delete(masterId);

        verify(masterRepository).delete(master);
    }

    @Test
    void delete_shouldDoNothingWhenMasterNotFound() {
        int masterId = 1;
        when(masterRepository.findById(masterId)).thenReturn(Optional.empty());

        masterService.delete(masterId);

        verify(masterRepository, never()).delete(any());
    }

    @Test
    void freeMaster_shouldClearOrderAtWorkAndSaveWhenMasterExists() {
        int masterId = 1;
        Master master = new Master("Ivan", "Petrov");
        master.setId(masterId);
        master.setOrderAtWork(mock(Order.class));
        when(masterRepository.findById(masterId)).thenReturn(Optional.of(master));

        masterService.freeMaster(masterId);

        assertNull(master.getOrderAtWork());
        verify(masterRepository).save(master);
    }

    @Test
    void freeMaster_shouldDoNothingWhenMasterNotFound() {
        int masterId = 1;
        when(masterRepository.findById(masterId)).thenReturn(Optional.empty());

        masterService.freeMaster(masterId);

        verify(masterRepository, never()).save(any(Master.class));
    }

    @Test
    void find_shouldReturnDtoWhenMasterExists() {
        int masterId = 1;
        Master master = new Master("Иван", "Иванович");
        master.setId(masterId);
        master.setOrderAtWork(null);
        when(masterRepository.findById(masterId)).thenReturn(Optional.of(master));

        MasterDto result = masterService.find(masterId);

        assertNotNull(result);
        assertEquals(masterId, result.getId());
        assertEquals("Иван", result.getFirstname());
        assertEquals("Иванович", result.getLastname());
        assertEquals("free", result.getStatus());
        assertNull(result.getOrderAtWorkId());
    }

    @Test
    void find_shouldThrowEntityNotFoundExceptionWhenMasterNotFound() {
        int masterId = 1;
        when(masterRepository.findById(masterId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> masterService.find(masterId)
        );
        assertTrue(ex.getMessage().contains("Мастер не найден"));
        assertTrue(ex.getMessage().contains(String.valueOf(masterId)));
    }

    @Test
    void findAll_shouldConvertEachEntityToDto() {
        SortParams sortParams = new SortParams(SortCriteria.ID, SortDirection.ASC);
        Master master1 = new Master("Иван", "Иванович");
        master1.setId(1);
        Master master2 = new Master("Пётр", "Петрович");
        master2.setId(2);
        when(masterRepository.findSorted(sortParams)).thenReturn(List.of(master1, master2));

        List<MasterDto> result = masterService.findAll(sortParams);

        assertNotNull(result);
        assertEquals(2, result.size());
        // Первый DTO
        assertEquals(1, result.get(0).getId());
        assertEquals("Иван", result.get(0).getFirstname());
        assertEquals("Иванович", result.get(0).getLastname());
        assertEquals("free", result.get(0).getStatus());
        assertNull(result.get(0).getOrderAtWorkId());
        // Второй DTO
        assertEquals(2, result.get(1).getId());
        assertEquals("Пётр", result.get(1).getFirstname());
        assertEquals("Петрович", result.get(1).getLastname());
        assertEquals("free", result.get(1).getStatus());
        assertNull(result.get(1).getOrderAtWorkId());
    }

    @Test
    void findAll_shouldReturnEmptyListWhenRepositoryReturnsEmpty() {
        when(masterRepository.findSorted(null)).thenReturn(List.of());

        List<MasterDto> result = masterService.findAll(null);

        assertTrue(result.isEmpty());
    }

    @Test
    void create_shouldSaveMasterAndReturnDto() {
        SaveMasterDto dto = new SaveMasterDto();
        dto.setFirstname("Иван");
        dto.setLastname("Иванович");
        Master savedMaster = new Master("Иван", "Иванович");
        savedMaster.setId(1);
        when(masterRepository.save(any(Master.class))).thenReturn(savedMaster);

        MasterDto result = masterService.create(dto);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Иван", result.getFirstname());
        assertEquals("Иванович", result.getLastname());
        assertEquals("free", result.getStatus());
        assertNull(result.getOrderAtWorkId());
        verify(masterRepository).save(any(Master.class));
    }

    @Test
    void update_shouldUpdateExistingMasterAndReturnDto() {
        int masterId = 1;
        SaveMasterDto dto = new SaveMasterDto();
        dto.setFirstname("NewFirst");
        dto.setLastname("NewLast");
        Master existingMaster = new Master("OldFirst", "OldLast");
        existingMaster.setId(masterId);
        when(masterRepository.findById(masterId)).thenReturn(Optional.of(existingMaster));
        when(masterRepository.save(any(Master.class))).thenAnswer(inv -> inv.getArgument(0));

        MasterDto result = masterService.update(masterId, dto);

        assertNotNull(result);
        assertEquals(masterId, result.getId());
        assertEquals("NewFirst", result.getFirstname());
        assertEquals("NewLast", result.getLastname());
        assertEquals("free", result.getStatus());
        assertNull(result.getOrderAtWorkId());
        verify(masterRepository).save(existingMaster);
        assertEquals("NewFirst", existingMaster.getFirstname());
        assertEquals("NewLast", existingMaster.getLastname());
    }

    @Test
    void update_shouldThrowEntityNotFoundExceptionWhenMasterNotFound() {
        int masterId = 1;
        SaveMasterDto dto = new SaveMasterDto();
        dto.setFirstname("Any");
        dto.setLastname("Any");
        when(masterRepository.findById(masterId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(
                EntityNotFoundException.class,
                () -> masterService.update(masterId, dto)
        );
        assertTrue(ex.getMessage().contains("Мастер не найден"));
        verify(masterRepository, never()).save(any(Master.class));
    }
}