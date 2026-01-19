package carservice.dao;

import carservice.common.Period;
import carservice.models.garage.GarageSpot;
import carservice.models.garage.GarageSpotStatus;

import java.util.Arrays;
import java.util.List;

public interface GarageSpotDAO extends DAO<GarageSpotDTO> {
    GarageSpotDTO findByNumber(int number);
    List<GarageSpotDTO> findFreeGarageSpotsInPeriod(Period period);
    List<GarageSpotDTO> findFilteredByStatus(GarageSpotStatus status);
}
