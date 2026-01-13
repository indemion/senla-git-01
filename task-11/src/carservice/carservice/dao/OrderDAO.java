package carservice.dao;

import carservice.models.order.FilterParams;
import carservice.models.order.Order;
import carservice.models.order.SortParams;

import java.util.List;

public interface OrderDAO extends DAO<OrderDTO> {
    List<OrderDTO> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams);
}
