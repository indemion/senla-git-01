package ru.indemion.carservice.dao;

import ru.indemion.carservice.models.order.FilterParams;
import ru.indemion.carservice.models.order.SortParams;

import java.util.List;

public interface OrderDAO extends DAO<OrderDTO> {
    List<OrderDTO> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams);
}
