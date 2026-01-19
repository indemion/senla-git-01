package carservice.models.services;

import carservice.common.Period;
import carservice.models.master.Master;
import carservice.models.master.MasterService;
import carservice.models.order.Order;
import carservice.models.order.OrderService;
import di.Inject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FreePeriodService {
    private final OrderService orderService;
    private final MasterService masterService;

    @Inject
    public FreePeriodService(OrderService orderService, MasterService masterService) {
        this.orderService = orderService;
        this.masterService = masterService;
    }

    public Period getClosestFreePeriodWithDuration(Duration requiredDuration) {
        List<List<Period>> occupiedPeriodsGroupedByMaster = new ArrayList<>();
        masterService.getMasters().forEach(master -> {
            List<Order> orders = orderService.getOrdersByMasterCreatedAndWIP(master);
            occupiedPeriodsGroupedByMaster.add(orders.stream().map(Order::getEstimatedWorkPeriod)
                    .collect(Collectors.toList()));
        });
        occupiedPeriodsGroupedByMaster.forEach(periods -> periods.sort(Comparator.comparing(Period::getStart)));

        List<Period> freePeriods = new ArrayList<>();
        for (List<Period> occupiedPeriods : occupiedPeriodsGroupedByMaster) {
            freePeriods.add(findClosestFreePeriod(occupiedPeriods, requiredDuration));
        }
        freePeriods.sort(Comparator.comparing(Period::getStart));

        return freePeriods.stream().findFirst().get();
    }

    private Period findClosestFreePeriod(List<Period> occupiedPeriods, Duration requiredDuration) {
        List<Period> periods = new ArrayList<>(occupiedPeriods);
        periods.sort(Comparator.comparing(Period::getStart));
        LocalDateTime currentPointer = LocalDateTime.now();
        for (Period period : periods) {
            // Проверяем что текущий указатель времени находится раньше ближайшего периода
            if (currentPointer.isBefore(period.getStart())) {
                // Проверяем что между текущим указателем времени и началом следующего периода есть запрашиваемая
                // длительность
                if (Duration.between(currentPointer, period.getStart()).compareTo(requiredDuration) >= 0) {
                    return new Period(currentPointer, currentPointer.plus(requiredDuration));
                } else {
                    // Если между текущим указателем и началом периода недостаточно времени, то меняем текущий указатель
                    // на конец периода
                    currentPointer = period.getEnd();
                }
            }
            if (currentPointer.isBefore(period.getEnd())) {
                // Если текущий указатель оказался внутри периода, то меняем его на конец периода
                currentPointer = period.getEnd();
            }
        }

        return new Period(currentPointer, currentPointer.plus(requiredDuration));
    }
}
