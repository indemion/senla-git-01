package carservice.models.repositories;

import carservice.common.Period;
import carservice.models.garage.GarageSpot;
import carservice.models.garage.GarageSpotStatus;

import java.util.List;
import java.util.Optional;

public interface GarageSpotRepository extends Repository<GarageSpot> {
    Optional<GarageSpot> findByNumber(int number);
    List<GarageSpot> findFreeGarageSpotsInPeriod(Period period);
    List<GarageSpot> findFilteredByStatus(GarageSpotStatus status);
}
