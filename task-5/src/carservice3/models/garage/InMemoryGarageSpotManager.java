package carservice3.models.garage;

import carservice3.common.Period;
import carservice3.models.master.InMemoryMasterManager;
import carservice3.models.order.InMemoryOrderManager;
import carservice3.models.order.Order;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryGarageSpotManager {
    private static InMemoryGarageSpotManager instance;
    private final List<GarageSpot> spots = new ArrayList<>();
    private InMemoryOrderManager orderManager;
    private InMemoryMasterManager masterManager;

    private InMemoryGarageSpotManager() {
    }

    public static InMemoryGarageSpotManager instance() {
        if (instance == null) {
            instance = new InMemoryGarageSpotManager();
        }

        return instance;
    }

    public void setOrderManager(InMemoryOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public void setMasterManager(InMemoryMasterManager masterManager) {
        this.masterManager = masterManager;
    }

    public GarageSpot create(int number) {
        Optional<GarageSpot> optionalGarageSpot = findByNumber(number);
        if (optionalGarageSpot.isPresent()) {
            return optionalGarageSpot.get();
        }
        GarageSpot spot = new GarageSpot(number);
        spots.add(spot);

        return spot;
    }

    public void remove(int number) {
        findByNumber(number).ifPresent(spots::remove);
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
        return query().filterByStatus(GarageSpotStatus.FREE).get();
    }

    public int getFreeGarageSpotsCountAtDate(LocalDate localDate) {
        Period period = new Period(localDate.atStartOfDay(), localDate.atTime(LocalTime.MAX));
        List<Order> orders = orderManager.getOrders().stream().filter(order -> order.getEstimatedWorkPeriod()
                .isOverlap(period)).toList();
        Set<Integer> busyMasterIds = new HashSet<>();
        for (Order order : orders) {
            busyMasterIds.add(order.getMaster().getId());
        }
        int freeSpotsQuantity = getFreeGarageSpotsInPeriod(period).size();
        int freeMastersQuantity = masterManager.getMastersWhereIdNotIn(busyMasterIds).size();

        return Math.min(freeMastersQuantity, freeSpotsQuantity);
    }

    public List<GarageSpot> getGarageSpots() {
        return query().get();
    }

    public List<GarageSpot> getGarageSpotsByStatus(GarageSpotStatus status) {
        return query().filterByStatus(status).get();
    }
}
