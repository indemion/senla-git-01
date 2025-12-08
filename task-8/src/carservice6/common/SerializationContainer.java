package carservice6.common;

import carservice6.models.garage.GarageSpotRepository;
import carservice6.models.master.MasterRepository;
import carservice6.models.order.OrderRepository;

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
