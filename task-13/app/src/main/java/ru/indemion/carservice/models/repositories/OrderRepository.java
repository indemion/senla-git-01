package ru.indemion.carservice.models.repositories;

import ru.indemion.carservice.models.order.FilterParams;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.order.SortParams;

import java.util.List;

public interface OrderRepository extends Repository<Order> {
    List<Order> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams);
}
