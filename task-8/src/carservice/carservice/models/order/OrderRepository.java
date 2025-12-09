package carservice.models.order;

import carservice.models.garage.GarageSpotRepository;
import carservice.models.master.MasterRepository;
import carservice.models.repositories.InMemoryRepository;

public class OrderRepository extends InMemoryRepository<Order> {
    private static OrderRepository instance;

    private OrderRepository() {
    }

    public static OrderRepository instance() {
        if (instance == null) {
            instance = new OrderRepository();
        }

        return instance;
    }

    public static void setInstance(OrderRepository instance) {
        OrderRepository.instance = instance;
    }

    public void restoreReferences(MasterRepository masterRepository, GarageSpotRepository garageSpotRepository) {
        inMemoryDB.forEach((integer, order) -> {
            masterRepository.findById(order.getMasterId()).ifPresent(order::setMaster);
            garageSpotRepository.findById(order.getGarageSpotId()).ifPresent(order::setGarageSpot);
        });
    }
}
