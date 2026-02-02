package ru.indemion.carservice.models.garage;

import org.springframework.stereotype.Service;
import ru.indemion.carservice.AppConfig;
import ru.indemion.carservice.common.OperationProhibitedMessages;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.exceptions.OperationProhibitedException;
import ru.indemion.carservice.models.repositories.GarageSpotRepository;
import ru.indemion.carservice.models.services.AbstractTransactionalService;
import ru.indemion.carservice.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

@Service
public class GarageSpotService extends AbstractTransactionalService {
    private final GarageSpotRepository garageSpotRepository;
    private final AppConfig appConfig;
    private final GarageSpotCsvExporter csvExporter;
    private final GarageSpotCsvImporter csvImporter;

    public GarageSpotService(GarageSpotRepository garageSpotRepository,
                             AppConfig appConfig, GarageSpotCsvExporter csvExporter, GarageSpotCsvImporter csvImporter) {
        super(HibernateUtil.getCurrentSession());
        this.garageSpotRepository = garageSpotRepository;
        this.appConfig = appConfig;
        this.csvExporter = csvExporter;
        this.csvImporter = csvImporter;
    }

    public GarageSpot create(int number) {
        Optional<GarageSpot> optionalGarageSpot = findByNumber(number);
        return optionalGarageSpot.orElseGet(() -> inTransaction(() -> garageSpotRepository.save(new GarageSpot(number))));
    }

    public void save(GarageSpot garageSpot) {
        garageSpotRepository.save(garageSpot);
    }

    public void delete(int id) {
        if (!appConfig.isGarageSpotRemovable()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.GARAGE_SPOT_REMOVING);
        }

        Optional<GarageSpot> optionalGarageSpot = garageSpotRepository.findByNumber(id);
        optionalGarageSpot.ifPresent(garageSpot -> inTransaction(() -> garageSpotRepository.delete(garageSpot)));
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

    public void freeGarageSpot(int garageSpotId) {
        Optional<GarageSpot> optionalGarageSpot = garageSpotRepository.findById(garageSpotId);
        optionalGarageSpot.ifPresent(garageSpot -> {
            garageSpot.setOrderAtWork(null);
            garageSpotRepository.save(garageSpot);
        });
    }

    public String exportToPath(String path) {
        return csvExporter.exportToPath(path, getGarageSpots());
    }

    public void importFromPath(String path) {
        List<GarageSpot> garageSpots = csvImporter.importFromPath(path);
        inTransaction(() -> garageSpotRepository.save(garageSpots));
    }
}
