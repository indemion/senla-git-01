package ru.indemion.carservice.api.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.indemion.carservice.dto.AvailableSlotsCountAtDateResponse;
import ru.indemion.carservice.models.services.AvailabilityService;

import java.time.LocalDate;

@RestController
public class AvailableSlotApiController {
    private final AvailabilityService availabilityService;

    public AvailableSlotApiController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @GetMapping("/available-slots-count")
    public AvailableSlotsCountAtDateResponse getAvailableSlotsCountAtDate(@RequestParam LocalDate date) {
        AvailableSlotsCountAtDateResponse response = new AvailableSlotsCountAtDateResponse();
        response.setCount(availabilityService.countAvailableSlotsAtDate(date));
        return response;
    }
}
