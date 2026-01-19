package ru.indemion.carservice.dao;

import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.garage.GarageSpotStatus;

import java.util.List;

public interface GarageSpotDAO extends DAO<GarageSpotDTO> {
    GarageSpotDTO findByNumber(int number);

    List<GarageSpotDTO> findFreeGarageSpotsInPeriod(Period period);

    List<GarageSpotDTO> findFilteredByStatus(GarageSpotStatus status);
}
