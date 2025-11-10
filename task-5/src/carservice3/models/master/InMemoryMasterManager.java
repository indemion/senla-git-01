package carservice3.models.master;

import carservice3.common.EntityNotFoundException;
import carservice3.common.Period;
import carservice3.models.order.InMemoryOrderManager;
import carservice3.models.order.Order;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMasterManager {
    private static InMemoryMasterManager instance;
    private final List<Master> masters = new ArrayList<>();
    private InMemoryOrderManager orderManager;

    private InMemoryMasterManager() {
    }

    public static InMemoryMasterManager instance() {
        if (instance == null) {
            instance = new InMemoryMasterManager();
        }

        return instance;
    }

    public void setOrderManager(InMemoryOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public Master create(String firstname, String lastname) {
        Master master = new Master(firstname, lastname);
        masters.add(master);

        return master;
    }

    public void remove(int id) {
        Optional<Master> optionalMaster = findById(id);
        optionalMaster.ifPresent(masters::remove);
    }

    public void remove(Master master) {
        Optional<Master> optionalMaster = findById(master.getId());
        optionalMaster.ifPresent(masters::remove);
    }

    public Optional<Master> findById(int id) {
        return masters.stream().filter((m) -> m.getId() == id).findFirst();
    }

    public Master getMasterByOrderId(int id) throws EntityNotFoundException {
        Optional<Order> optionalOrder = orderManager.findById(id);
        if (optionalOrder.isEmpty()) {
            throw new EntityNotFoundException("Заказ с id " + id + " не найден.");
        }

        return optionalOrder.get().getMaster();
    }

    public Query query() {
        return new Query(masters);
    }

    public List<Master> getMasters() {
        return query().get();
    }

    public List<Master> getMastersSorted(SortParam sortParam) {
        return query().orderBy(sortParam).get();
    }

    public List<Master> getMastersFilteredByStatus(MasterStatus status) {
        return query().filterByStatus(status).get();
    }

    public List<Master> getMastersWhereIdNotIn(Set<Integer> busyMasterIds) {
        return query().filterByIdNotIn(busyMasterIds).get();
    }

    public List<Master> getMastersFreeInPeriod(Period period) {
        return query().addPredicate(master ->
                orderManager.getOrdersByMasterAndEstimatedWorkPeriodOverlapPeriod(master, period).isEmpty()).get();
    }

    public Optional<Master> getOneMasterFreeInPeriod(Period period) {
        return getMastersFreeInPeriod(period).stream().findFirst();
    }

    public Period getClosestFreePeriodWithDuration(Duration requiredDuration) {
        List<List<Period>> periodsList = new ArrayList<>();
        query().get().forEach(master -> {
            List<Order> orders = orderManager.getOrdersByMasterCreatedAndWIP(master);
            periodsList.add(orders.stream().map(Order::getEstimatedWorkPeriod).collect(Collectors.toList()));
        });
        periodsList.forEach(periods -> periods.sort(Comparator.comparing(Period::getStart)));

        List<Period> freePeriods = new ArrayList<>();
        for (List<Period> periods : periodsList) {
            freePeriods.add(FreePeriodFinder.findClosestFreePeriod(periods, requiredDuration));
        }
        freePeriods.sort(Comparator.comparing(Period::getStart));

        return freePeriods.stream().findFirst().get();
    }

}
