package carservice3.seeds;

import carservice3.common.Period;
import carservice3.models.garage.GarageSpot;
import carservice3.models.garage.InMemoryGarageSpotManager;
import carservice3.models.master.InMemoryMasterManager;
import carservice3.models.master.Master;
import carservice3.models.order.InMemoryOrderManager;
import carservice3.models.order.Order;
import carservice3.models.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class MainSeed {
    private static final Random random = new Random();
    private static final InMemoryMasterManager masterManager = InMemoryMasterManager.instance();
    private static final InMemoryOrderManager orderManager = InMemoryOrderManager.instance();
    private static final InMemoryGarageSpotManager garageSpotManager = InMemoryGarageSpotManager.instance();

    public static void run() {
        initMasters();
        initGarageSpots();
        initOrders();
    }

    private static void initMasters() {
        masterManager.create("Пётр", "Петрович");
        masterManager.create("Афанасий", "Афанасич");
        masterManager.create("Михал", "Михалыч");
    }

    private static void initGarageSpots() {
        for (int i = 1; i <= 7; i++) {
            garageSpotManager.create(i);
        }
    }

    private static void initOrders() {
        Optional<GarageSpot> optionalGarageSpot;
        Optional<Master> optionalFreeMaster;
        Period period;
        for (int i = 0; i < 10; i++) {
            period = new Period(LocalDateTime.now().plusHours(i), LocalDateTime.now().plusHours(i + 1));
            optionalGarageSpot = garageSpotManager.getOneGarageSpotFreeInPeriod(period);
            List<Master> freeMastersInPeriod = masterManager.getMastersFreeInPeriod(period);
            optionalFreeMaster = Optional.ofNullable(freeMastersInPeriod.get(random.nextInt(freeMastersInPeriod.size())));
            if (optionalFreeMaster.isPresent() && optionalGarageSpot.isPresent()) {
                orderManager.create((i + 1) * 100, optionalFreeMaster.get(), optionalGarageSpot.get(), period.getStart(),
                        period.getEnd());
            }
        }

        List<Order> createdOrders = orderManager.getOrdersFilteredByStatus(OrderStatus.CREATED);
        for (Order order : createdOrders) {
            orderManager.startWorking(order.getId());
        }
    }
}
