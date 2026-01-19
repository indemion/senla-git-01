package carservice.models.services;

import carservice.common.Period;
import carservice.models.garage.GarageSpotService;
import carservice.models.master.MasterService;
import di.Inject;

import java.time.LocalDate;
import java.time.LocalTime;

public class AvailabilityService {
    private final GarageSpotService garageSpotService;
    private final MasterService masterService;

    @Inject
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
