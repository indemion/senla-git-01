package carservice.common;

import carservice.models.garage.GarageSpotRepository;
import carservice.models.master.MasterRepository;
import carservice.models.order.OrderRepository;

import java.io.Serializable;

public record SerializationContainer(OrderRepository orderRepository, MasterRepository masterRepository,
                                     GarageSpotRepository garageSpotRepository) implements Serializable {
    public void restoreReferences() {
        OrderRepository.setInstance(orderRepository);
        MasterRepository.setInstance(masterRepository);
        GarageSpotRepository.setInstance(garageSpotRepository);
        orderRepository.restoreReferences(masterRepository, garageSpotRepository);
        masterRepository.restoreReferences(orderRepository);
        garageSpotRepository.restoreReferences(orderRepository);
    }
}
