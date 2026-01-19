package carservice.models.repositories;

import carservice.dao.OrderDAO;
import carservice.dao.OrderDTO;
import carservice.models.order.FilterParams;
import carservice.models.order.Order;
import carservice.models.order.SortParams;
import di.Inject;

import java.util.List;
import java.util.Optional;

public class DBOrderRepository implements OrderRepository {
    private final OrderDAO orderDAO;
    private final OrderMapper mapper;

    @Inject
    public DBOrderRepository(OrderDAO orderDAO, OrderMapper mapper) {
        this.orderDAO = orderDAO;
        this.mapper = mapper;
    }

    @Override
    public Order save(Order model) {
        OrderDTO orderDTO = orderDAO.save(mapper.modelToEntity(model));
        model.setId(orderDTO.id());
        return model;
    }

    @Override
    public void save(List<Order> models) {
        models.forEach(order -> orderDAO.save(mapper.modelToEntity(order)));
    }

    @Override
    public void delete(Order model) {
        orderDAO.delete(model.getId());
    }

    @Override
    public Optional<Order> findById(int id) {
        OrderDTO orderDTO = orderDAO.findById(id);
        if (orderDTO != null) {
            return Optional.of(mapper.entityToModel(orderDTO));
        }

        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        return findFilteredAndSorted(null, null);
    }

    @Override
    public List<Order> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams) {
        return orderDAO.findFilteredAndSorted(filterParams, sortParams).stream()
                .map(mapper::entityToModel).toList();
    }
}
