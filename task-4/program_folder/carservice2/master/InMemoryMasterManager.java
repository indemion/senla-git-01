package carservice2.master;

import carservice2.order.InMemoryOrderManager;
import carservice2.order.Order;
import carservice2.common.Period;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryMasterManager {
    private final List<Master> masters = new ArrayList<>();
    private InMemoryOrderManager orderManager;

    public void setOrderManager(InMemoryOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public Master create(String firstname, String lastname) {
        Master master = new Master(firstname, lastname);
        masters.add(master);
        System.out.printf("Добавлен мастер \"%s\"%n", master.getFullname());
        return master;
    }

    public void remove(Master master) {
        Optional<Master> optionalMaster = findById(master.getId());
        if (optionalMaster.isEmpty()) {
            return;
        }
        masters.remove(master);
        System.out.printf("Удалён мастер \"%s\"%n", master.getFullname());
    }

    public Optional<Master> findById(int id) {
        return masters.stream().filter((m) -> m.getId() == id).findFirst();
    }

    public Master getMasterByOrder(Order order) {
        return order.getMaster();
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
