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
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.common.SortDirection;
import ru.indemion.carservice.dto.CreateOrderDto;
import ru.indemion.carservice.dto.OrderDto;
import ru.indemion.carservice.models.order.FilterParams;
import ru.indemion.carservice.models.order.OrderService;
import ru.indemion.carservice.models.order.OrderStatus;
import ru.indemion.carservice.models.order.SortCriteria;
import ru.indemion.carservice.models.order.SortParams;
import ru.indemion.carservice.util.Util;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderApiController {
    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderDto> findAll(
            @RequestParam(required = false) String statuses,
            @RequestParam(required = false) Integer masterId,
            @RequestParam(required = false) String stringPeriod,
            @RequestParam(defaultValue = "ID") SortCriteria sortBy,
            @RequestParam(defaultValue = "ASC") SortDirection sortOrder) {
        FilterParams.Builder filterParamsBuilder = FilterParams.builder();
        if (statuses != null) {
            filterParamsBuilder.statuses(Arrays.stream(statuses.split(",")).map(OrderStatus::parse).toList());
        }
        if (masterId != null) {
            filterParamsBuilder.masterId(masterId);
        }
        if (stringPeriod != null) {
            String[] stringDates = stringPeriod.split(",");
            if (stringDates.length != 2) {
                throw new IllegalArgumentException("Некорректный формат фильтра period");
            }
            Period period = new Period(LocalDateTime.parse(stringDates[0]), LocalDateTime.parse(stringDates[1]));
            filterParamsBuilder.estimatedWorkStartInPeriod(period);
        }
        SortParams sortParams = new SortParams(sortBy, sortOrder);
        return orderService.findAll(filterParamsBuilder.build(), sortParams);
    }

    @PostMapping
    public OrderDto create(@RequestBody CreateOrderDto createOrderDto) {
        return orderService.create(createOrderDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        orderService.delete(id);
    }

    @PostMapping("/{id}/start")
    public OrderDto startWorking(@PathVariable int id) {
        return orderService.start(id);
    }

    @PostMapping("/{id}/close")
    public OrderDto close(@PathVariable int id) {
        return orderService.close(id);
    }

    @PostMapping("/{id}/cancel")
    public OrderDto cancel(@PathVariable int id) {
        return orderService.cancel(id);
    }

    @PostMapping("/shift-estimated-work-period")
    public void shiftOrdersEstimatedWorkPeriod(@RequestParam int duration) {
        orderService.shiftEstimatedWorkPeriod(duration);
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportCsv() {
        return Util.getResponseEntityForCsvData(orderService.getCsvData(), "orders");
    }

    @PostMapping("/import")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void importCsv(@RequestParam MultipartFile file) {
        orderService.importCsv(file);
    }
}
