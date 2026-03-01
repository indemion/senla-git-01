package ru.indemion.carservice.api.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.SortDirection;
import ru.indemion.carservice.dto.MasterDto;
import ru.indemion.carservice.dto.SaveMasterDto;
import ru.indemion.carservice.models.master.MasterService;
import ru.indemion.carservice.models.master.SortCriteria;
import ru.indemion.carservice.models.master.SortParams;
import ru.indemion.carservice.util.Util;

import java.util.List;

@RestController
@RequestMapping("/masters")
public class MasterApiController {
    private final MasterService masterService;

    public MasterApiController(MasterService masterService) {
        this.masterService = masterService;
    }

    @GetMapping
    public List<MasterDto> findAll(
            @RequestParam(defaultValue = "ID") SortCriteria sortBy,
            @RequestParam(defaultValue = "ASC") SortDirection sortOrder) {
        SortParams sortParams = new SortParams(sortBy, sortOrder);
        return masterService.findAll(sortParams);
    }

    @GetMapping("/{id}")
    public MasterDto findById(@PathVariable int id) {
        return masterService.find(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MasterDto create(@RequestBody SaveMasterDto masterDto) {
        return masterService.create(masterDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable int id, @RequestBody SaveMasterDto masterDto) {
        masterService.update(id, masterDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        masterService.delete(id);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        return Util.getResponseEntityForCsvData(masterService.getCsvData(), "masters");
    }

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void importCsv(@RequestParam MultipartFile file) {
        masterService.importCsv(file);
    }
}
