package ru.indemion.carservice.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.SortDirection;
import ru.indemion.carservice.common.SortParams;
import ru.indemion.carservice.dto.CreateGarageSpotDto;
import ru.indemion.carservice.dto.GarageSpotDto;
import ru.indemion.carservice.models.garage.FilterParams;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.garage.GarageSpotStatus;
import ru.indemion.carservice.models.garage.SortCriteria;
import ru.indemion.carservice.util.Util;

import java.util.List;

@RestController
@RequestMapping("/garage-spots")
public class GarageSpotApiController {
    private final GarageSpotService garageSpotService;

    public GarageSpotApiController(GarageSpotService garageSpotService) {
        this.garageSpotService = garageSpotService;
    }

    @GetMapping
    public List<GarageSpotDto> findAll(
            @RequestParam(required = false) GarageSpotStatus status,
            @RequestParam(defaultValue = "ID") SortCriteria sortBy,
            @RequestParam(defaultValue = "ASC") SortDirection sortOrder) {
        FilterParams filterParams = new FilterParams(status);
        SortParams<SortCriteria> sortParams = new SortParams<>(sortBy, sortOrder);
        return garageSpotService.findAll(filterParams, sortParams);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GarageSpotDto create(@RequestBody CreateGarageSpotDto createGarageSpotDto) {
        return garageSpotService.createOrGet(createGarageSpotDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        garageSpotService.delete(id);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        return Util.getResponseEntityForCsvData(garageSpotService.getCsvData(), "garage-spots");
    }

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void importCsv(@RequestParam MultipartFile file) {
        garageSpotService.importCsv(file);
    }
}
