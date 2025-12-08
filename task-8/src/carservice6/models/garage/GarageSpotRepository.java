package carservice6.models.garage;

import carservice6.models.order.OrderRepository;
import carservice6.models.repositories.InMemoryRepository;

public class GarageSpotRepository extends InMemoryRepository<GarageSpot> {
    private static GarageSpotRepository instance;

    private GarageSpotRepository() {
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
