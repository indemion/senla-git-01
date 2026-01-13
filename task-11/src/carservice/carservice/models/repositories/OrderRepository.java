package carservice.models.repositories;

import carservice.models.order.FilterParams;
import carservice.models.order.Order;
import carservice.models.order.SortParams;

import java.util.List;

public interface OrderRepository extends Repository<Order> {
    List<Order> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams);
}
