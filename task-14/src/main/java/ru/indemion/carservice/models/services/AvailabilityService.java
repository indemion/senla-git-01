package ru.indemion.carservice.models.services;

import org.springframework.stereotype.Service;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.master.MasterService;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
public class AvailabilityService {
    private final GarageSpotService garageSpotService;
    private final MasterService masterService;

    public AvailabilityService(GarageSpotService garageSpotService, MasterService masterService) {
        this.garageSpotService = garageSpotService;
        this.masterService = masterService;
    }

    public int countAvailableSlotsAtDate(LocalDate localDate) {
        Period period = new Period(localDate.atStartOfDay(), localDate.atTime(LocalTime.MAX));
        int freeSpotsQuantity = garageSpotService.getFreeGarageSpotsInPeriod(period).size();
        int freeMastersQuantity = masterService.getMastersFreeInPeriod(period).size();

        return Math.min(freeMastersQuantity, freeSpotsQuantity);
    }
}
