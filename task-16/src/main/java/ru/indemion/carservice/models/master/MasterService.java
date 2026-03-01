package ru.indemion.carservice.models.master;

import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.dto.MasterDto;
import ru.indemion.carservice.dto.SaveMasterDto;

import java.util.List;
import java.util.Optional;

public interface MasterService {
    Master create(String firstname, String lastname);
    void delete(int id);
    Optional<Master> findById(int id);
    Optional<Master> getMasterByOrderId(int id);
    List<Master> getMasters();
    List<Master> getMastersSorted(SortParams sortParams);
    List<Master> getMastersFreeInPeriod(Period period);
    String exportToPath(String path);
    void importFromPath(String path);
    void freeMaster(int masterId);
    void save(Master master);
    MasterDto find(int id);
    List<MasterDto> findAll(SortParams sortParams);
    MasterDto create(SaveMasterDto masterDto);
    MasterDto update(int id, SaveMasterDto masterDto);
    String getCsvData();
    void importCsv(MultipartFile file);
}
