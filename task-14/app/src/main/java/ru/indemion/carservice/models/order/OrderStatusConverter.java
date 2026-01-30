package ru.indemion.carservice.models.order;

import jakarta.persistence.AttributeConverter;

public class OrderStatusConverter implements AttributeConverter<OrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(OrderStatus orderStatus) {
        return orderStatus.toString();
    }

    @Override
    public OrderStatus convertToEntityAttribute(String s) {
        return OrderStatus.parse(s);
    }
}
