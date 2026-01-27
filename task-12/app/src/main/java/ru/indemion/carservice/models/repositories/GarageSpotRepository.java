package ru.indemion.carservice.models.repositories;

import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotStatus;

import java.util.List;
import java.util.Optional;

public interface GarageSpotRepository extends Repository<GarageSpot> {
    Optional<GarageSpot> findByNumber(int number);

    List<GarageSpot> findFreeGarageSpotsInPeriod(Period period);

    List<GarageSpot> findFilteredByStatus(GarageSpotStatus status);
}
