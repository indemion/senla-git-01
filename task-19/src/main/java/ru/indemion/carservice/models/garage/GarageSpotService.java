package ru.indemion.carservice.models.garage;

import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.common.SortParams;
import ru.indemion.carservice.dto.CreateGarageSpotDto;
import ru.indemion.carservice.dto.GarageSpotDto;

import java.util.List;
import java.util.Optional;

public interface GarageSpotService {
    GarageSpot create(int number);
    void save(GarageSpot garageSpot);
    void delete(int id);
    List<GarageSpot> getFreeGarageSpotsInPeriod(Period period);
    Optional<GarageSpot> findById(int id);
    Optional<GarageSpot> findByNumber(int number);
    List<GarageSpot> getGarageSpots();
    List<GarageSpot> getGarageSpotsByStatus(GarageSpotStatus status);
    void freeGarageSpot(int garageSpotId);
    String exportToPath(String path);
    void importFromPath(String path);
    List<GarageSpotDto> findAll(FilterParams filterParams, SortParams<SortCriteria> sortParams);
    GarageSpotDto create(CreateGarageSpotDto createGarageSpotDto);
    String getCsvData();
    void importCsv(MultipartFile file);
}
