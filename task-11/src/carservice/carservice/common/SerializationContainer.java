package carservice.common;

import carservice.models.garage.GarageSpot;
import carservice.models.master.Master;
import carservice.models.order.Order;

import java.io.Serializable;
import java.util.List;

public record SerializationContainer(List<Order> orders, List<Master> masters,
                                     List<GarageSpot> garageSpots) implements Serializable {
}
