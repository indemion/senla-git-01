package carservice.models.garage;

import carservice.models.order.OrderRepository;
import carservice.models.repositories.InMemoryRepository;

public class GarageSpotRepository extends InMemoryRepository<GarageSpot> {
    private static GarageSpotRepository instance;

    public GarageSpotRepository() {
    }

    public static GarageSpotRepository instance() {
        if (instance == null) {
            instance = new GarageSpotRepository();
        }

        return instance;
    }

    public static void setInstance(GarageSpotRepository instance) {
        GarageSpotRepository.instance = instance;
    }

    public void restoreReferences(OrderRepository orderRepository) {
        inMemoryDB.forEach((integer, garageSpot) -> {
            if (garageSpot.getOrderAtWorkId() != null) {
                orderRepository.findById(garageSpot.getOrderAtWorkId()).ifPresent(garageSpot::setOrderAtWork);
            }
        });
    }
}
