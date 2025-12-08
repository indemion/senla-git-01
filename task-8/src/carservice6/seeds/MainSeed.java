package carservice6.seeds;

import carservice6.common.Period;
import carservice6.models.garage.GarageSpot;
import carservice6.models.garage.GarageSpotService;
import carservice6.models.master.Master;
import carservice6.models.master.MasterService;
import carservice6.models.order.Order;
import carservice6.models.order.OrderService;
import carservice6.models.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class MainSeed {
    private static final Random random = new Random();
    private static final MasterService masterService = MasterService.instance();
    private static final OrderService orderService = OrderService.instance();
    private static final GarageSpotService garageSpotService = GarageSpotService.instance();

    public static void run() {
        initMasters();
        initGarageSpots();
        initOrders();
    }

    private static void initMasters() {
        masterService.create("Пётр", "Петрович");
        masterService.create("Афанасий", "Афанасич");
        masterService.create("Михал", "Михалыч");
    }

    private static void initGarageSpots() {
        for (int i = 1; i <= 7; i++) {
            garageSpotService.create(i);
        }
    }

    private static void initOrders() {
        Optional<GarageSpot> optionalGarageSpot;
        Optional<Master> optionalFreeMaster;
        Period period;
        for (int i = 0; i < 10; i++) {
            period = new Period(LocalDateTime.now().plusHours(i), LocalDateTime.now().plusHours(i + 1));
            optionalGarageSpot = garageSpotService.getOneGarageSpotFreeInPeriod(period);
            List<Master> freeMastersInPeriod = masterService.getMastersFreeInPeriod(period);
            optionalFreeMaster = Optional.ofNullable(freeMastersInPeriod.get(random.nextInt(freeMastersInPeriod.size())));
            if (optionalFreeMaster.isPresent() && optionalGarageSpot.isPresent()) {
                orderService.create((i + 1) * 100, optionalFreeMaster.get(), optionalGarageSpot.get(),
                        period.getStart(), period.getEnd());
            }
        }

        List<Order> createdOrders = orderService.getOrdersFilteredByStatus(OrderStatus.CREATED);
        for (Order order : createdOrders) {
            orderService.startWorking(order.getId());
        }
    }
}
