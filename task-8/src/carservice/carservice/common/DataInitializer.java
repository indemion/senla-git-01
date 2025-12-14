package carservice.common;

import carservice.models.garage.GarageSpotRepository;
import carservice.models.master.MasterRepository;
import carservice.models.order.OrderRepository;
import di.Inject;

public class DataInitializer {
    private final OrderRepository orderRepository;
    private final MasterRepository masterRepository;
    private final GarageSpotRepository garageSpotRepository;

    @Inject
    public DataInitializer(OrderRepository orderRepository, MasterRepository masterRepository,
                           GarageSpotRepository garageSpotRepository) {
        this.orderRepository = orderRepository;
        this.masterRepository = masterRepository;
        this.garageSpotRepository = garageSpotRepository;
    }

    public void init(SerializationContainer serializationContainer) {
        orderRepository.setAll(serializationContainer.orders());
        masterRepository.setAll(serializationContainer.masters());
        garageSpotRepository.setAll(serializationContainer.garageSpots());

        orderRepository.restoreReferences(masterRepository, garageSpotRepository);
        masterRepository.restoreReferences(orderRepository);
        garageSpotRepository.restoreReferences(orderRepository);
    }
}
