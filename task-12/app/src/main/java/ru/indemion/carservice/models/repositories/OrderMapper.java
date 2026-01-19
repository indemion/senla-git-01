package ru.indemion.carservice.models.repositories;

import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.dao.OrderDTO;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.order.OrderStatus;

public class OrderMapper implements Mapper<Order, OrderDTO> {
    @Override
    public Order entityToModel(OrderDTO entity) {
        return new Order(entity.id(), entity.price(), OrderStatus.parse(entity.status()), entity.masterId(),
                entity.garageSpotId(), Period.newInstance(entity.estimatedWorkPeriodStart(), entity.estimatedWorkPeriodEnd()),
                Period.newInstance(entity.actualWorkPeriodStart(), entity.actualWorkPeriodEnd()), entity.createdAt(),
                entity.closedAt(), entity.canceledAt(), entity.deletedAt());
    }

    @Override
    public OrderDTO modelToEntity(Order model) {
        return new OrderDTO(model.getId(), model.getPrice(), model.getMasterId(), model.getGarageSpotId(),
                model.getStatus().toString(), model.getEstimatedWorkPeriod().getStart(),
                model.getEstimatedWorkPeriod().getEnd(), model.getActualWorkPeriod().getStart(),
                model.getActualWorkPeriod().getEnd(), model.getCreatedAt(), model.getClosedAt(), model.getCanceledAt(),
                model.getDeletedAt());
    }
}
