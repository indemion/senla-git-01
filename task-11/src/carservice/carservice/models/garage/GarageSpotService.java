package carservice.models.garage;

import carservice.AppConfig;
import carservice.common.OperationProhibitedMessages;
import carservice.common.Period;
import carservice.exceptions.OperationProhibitedException;
import carservice.models.repositories.GarageSpotRepository;
import di.Container;
import di.Inject;

import java.util.List;
import java.util.Optional;

public class GarageSpotService {
    private final GarageSpotRepository garageSpotRepository;
    private final AppConfig appConfig;

    @Inject
    public GarageSpotService(GarageSpotRepository garageSpotRepository, AppConfig appConfig) {
        this.garageSpotRepository = garageSpotRepository;
        this.appConfig = appConfig;
    }

    public GarageSpot create(int number) {
        Optional<GarageSpot> optionalGarageSpot = findByNumber(number);
        return optionalGarageSpot.orElseGet(() -> garageSpotRepository.save(new GarageSpot(number)));
    }

    public void delete(int id) {
        if (!appConfig.isGarageSpotRemovable()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.GARAGE_SPOT_REMOVING);
        }

        Optional<GarageSpot> optionalGarageSpot = garageSpotRepository.findById(id);
        optionalGarageSpot.ifPresent(garageSpotRepository::delete);
    }

    public List<GarageSpot> getFreeGarageSpotsInPeriod(Period period) {
        return garageSpotRepository.findFreeGarageSpotsInPeriod(period);
    }

    public Optional<GarageSpot> findById(int id) {
        return garageSpotRepository.findById(id);
    }

    public Optional<GarageSpot> findByNumber(int number) {
        return garageSpotRepository.findByNumber(number);
    }

    public List<GarageSpot> getGarageSpots() {
        return garageSpotRepository.findAll();
    }

    public List<GarageSpot> getGarageSpotsByStatus(GarageSpotStatus status) {
        return garageSpotRepository.findFilteredByStatus(status);
    }

    public String exportToPath(String path) {
        CsvExporter csvExporter = new CsvExporter();
        return csvExporter.exportToPath(path, getGarageSpots());
    }

    public void importFromPath(String path) {
        CsvImporter csvImporter = Container.INSTANCE.resolve(CsvImporter.class);
        List<GarageSpot> garageSpots = csvImporter.importFromPath(path);
        garageSpotRepository.save(garageSpots);
    }

    public void freeGarageSpot(int garageSpotId) {
        Optional<GarageSpot> optionalGarageSpot = garageSpotRepository.findById(garageSpotId);
        optionalGarageSpot.ifPresent(garageSpot -> {
            garageSpot.setOrderAtWork(null);
            garageSpotRepository.save(garageSpot);
        });
    }

    public void save(GarageSpot garageSpot) {
        garageSpotRepository.save(garageSpot);
    }
}
