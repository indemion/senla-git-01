package ru.indemion.carservice.models.garage;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.OperationProhibitedMessages;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.common.SortParams;
import ru.indemion.carservice.config.AppConfig;
import ru.indemion.carservice.dto.CreateGarageSpotDto;
import ru.indemion.carservice.dto.GarageSpotDto;
import ru.indemion.carservice.exceptions.OperationProhibitedException;
import ru.indemion.carservice.models.repositories.GarageSpotRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class GarageSpotServiceImpl implements GarageSpotService {
    private final GarageSpotRepository garageSpotRepository;
    private final AppConfig appConfig;
    private final GarageSpotCsvExporter csvExporter;
    private final GarageSpotCsvImporter csvImporter;
    private final GarageSpotCsvConverter garageSpotCsvConverter;

    public GarageSpotServiceImpl(GarageSpotRepository garageSpotRepository, AppConfig appConfig,
                                 GarageSpotCsvExporter csvExporter, GarageSpotCsvImporter csvImporter,
                                 GarageSpotCsvConverter garageSpotCsvConverter) {
        this.garageSpotRepository = garageSpotRepository;
        this.appConfig = appConfig;
        this.csvExporter = csvExporter;
        this.csvImporter = csvImporter;
        this.garageSpotCsvConverter = garageSpotCsvConverter;
    }

    @Override
    public GarageSpot create(int number) {
        Optional<GarageSpot> optionalGarageSpot = findByNumber(number);
        return optionalGarageSpot.orElseGet(() -> garageSpotRepository.save(new GarageSpot(number)));
    }

    @Override
    public void save(GarageSpot garageSpot) {
        garageSpotRepository.save(garageSpot);
    }

    @Override
    public void delete(int id) {
        if (!appConfig.isGarageSpotRemovable()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.GARAGE_SPOT_REMOVING);
        }

        Optional<GarageSpot> optionalGarageSpot = garageSpotRepository.findByNumber(id);
        optionalGarageSpot.ifPresent(garageSpotRepository::delete);
    }

    @Override
    public List<GarageSpot> getFreeGarageSpotsInPeriod(Period period) {
        return garageSpotRepository.findFreeGarageSpotsInPeriod(period);
    }

    @Override
    public Optional<GarageSpot> findById(int id) {
        return garageSpotRepository.findById(id);
    }

    @Override
    public Optional<GarageSpot> findByNumber(int number) {
        return garageSpotRepository.findByNumber(number);
    }

    @Override
    public List<GarageSpot> getGarageSpots() {
        return garageSpotRepository.findAll();
    }

    @Override
    public List<GarageSpot> getGarageSpotsByStatus(GarageSpotStatus status) {
        return garageSpotRepository.findFilteredByStatus(status);
    }

    @Override
    public void freeGarageSpot(int garageSpotId) {
        Optional<GarageSpot> optionalGarageSpot = garageSpotRepository.findById(garageSpotId);
        optionalGarageSpot.ifPresent(garageSpot -> {
            garageSpot.setOrderAtWork(null);
            garageSpotRepository.save(garageSpot);
        });
    }

    @Override
    public String exportToPath(String path) {
        return csvExporter.exportToPath(path, getGarageSpots());
    }

    @Override
    public void importFromPath(String path) {
        List<GarageSpot> garageSpots = csvImporter.importFromPath(path);
        garageSpotRepository.save(garageSpots);
    }

    @Override
    public List<GarageSpotDto> findAll(FilterParams filterParams, SortParams<SortCriteria> sortParams) {
        return garageSpotRepository.findFilteredAndSorted(filterParams, sortParams).stream()
                .map(this::convertEntityToDto).toList();
    }

    @Override
    public GarageSpotDto create(CreateGarageSpotDto createGarageSpotDto) {
        Optional<GarageSpot> optionalGarageSpot = findByNumber(createGarageSpotDto.getNumber());
        return convertEntityToDto(optionalGarageSpot.orElseGet(() ->
                garageSpotRepository.save(new GarageSpot(createGarageSpotDto.getNumber()))));
    }

    @Override
    public String getCsvData() {
        return garageSpotCsvConverter.convert(garageSpotRepository.findAll());
    }

    @Override
    public void importCsv(MultipartFile file) {
        List<GarageSpot> garageSpots = csvImporter.importFromMultipartFile(file);
        garageSpotRepository.save(garageSpots);
    }

    private GarageSpotDto convertEntityToDto(GarageSpot garageSpot) {
        return new GarageSpotDto(garageSpot.getId(), garageSpot.getNumber(), garageSpot.getStatus().toString(),
                garageSpot.getOrderAtWorkId());
    }
}
