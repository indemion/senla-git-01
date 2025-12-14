package carservice.models.master;

import carservice.models.order.OrderRepository;
import carservice.models.repositories.InMemoryRepository;

public class MasterRepository extends InMemoryRepository<Master> {
    private static MasterRepository instance;

    public MasterRepository() {
    }

    public static MasterRepository instance() {
        if (instance == null) {
            instance = new MasterRepository();
        }

        return instance;
    }

    public static void setInstance(MasterRepository instance) {
        MasterRepository.instance = instance;
    }

    public void restoreReferences(OrderRepository orderRepository) {
        inMemoryDB.forEach((integer, master) -> {
            if (master.getOrderAtWorkId() != null) {
                orderRepository.findById(master.getOrderAtWorkId()).ifPresent(master::setOrderAtWork);
            }
        });
    }
}
