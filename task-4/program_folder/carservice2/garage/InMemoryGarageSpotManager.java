package carservice2.garage;

import carservice2.common.Period;
import carservice2.master.InMemoryMasterManager;
import carservice2.order.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryGarageSpotManager {
    private final List<GarageSpot> spots = new ArrayList<>();
    private InMemoryOrderManager orderManager;

    public void setOrderManager(InMemoryOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public GarageSpot create(int number) {
        if (findByNumber(number).isPresent()) {
            System.out.printf("Место с номером %d уже существует в гараже, укажите другой номер%n", number);
            return null;
        }
        GarageSpot spot = new GarageSpot(number);
        spots.add(spot);
        System.out.printf("Место с номером %d добавлено в гараж%n", spot.getNumber());

        return spot;
    }

    public void remove(int number) {
        if (findByNumber(number).isEmpty()) {
            return;
        }

        spots.remove(number);
        System.out.printf("Место с номером %d удалено из гаража%n", number);
    }

    public List<GarageSpot> getFreeGarageSpotsOnDate(LocalDate localDate) {
        return getFreeGarageSpotsInPeriod(new Period(localDate.atStartOfDay(), localDate.atTime(LocalTime.MAX)));
    }

    public Optional<GarageSpot> getOneGarageSpotFreeInPeriod(Period period) {
        return getFreeGarageSpotsInPeriod(period).stream().findFirst();
    }

    public List<GarageSpot> getFreeGarageSpotsInPeriod(Period period) {
        return spots.stream().filter(spot -> {
            return orderManager.getOrdersFilteredByGarageSpotInPeriod(spot, period).isEmpty();
        }).collect(Collectors.toList());
    }

    public Optional<GarageSpot> findByNumber(int number) {
        return spots.stream().filter((gs) -> gs.getNumber() == number).findFirst();
    }

    public Query query() {
        return new Query(spots);
    }

    public List<GarageSpot> getFreeGarageSpots() {
        return query().filterByEmpty(true).get();
    }

    public int getFreeGarageSpotsQuantityAtDate(InMemoryMasterManager masterManager, LocalDate localDate) {
        Period period = new Period(localDate.atStartOfDay(), localDate.atTime(LocalTime.MAX));
        List<Order> orders = orderManager.getOrders().stream().filter(order -> order.getEstimatedWorkPeriod().isOverlap(period)).toList();
        Set<Integer> busyMasterIds = new HashSet<>();
        for (Order order : orders) {
            busyMasterIds.add(order.getMaster().getId());
        }
        int freeSpotsQuantity = getFreeGarageSpotsInPeriod(period).size();
        int freeMastersQuantity = masterManager.getMastersWhereIdNotIn(busyMasterIds).size();

        return Math.min(freeMastersQuantity, freeSpotsQuantity);
    }
}
