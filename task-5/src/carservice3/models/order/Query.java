package carservice3.models.order;

import carservice3.common.Period;
import carservice3.common.SortDirection;
import carservice3.models.master.Master;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Query extends carservice3.common.Query<Order> {
    public Query(List<Order> entities) {
        super(entities);
    }

    public Query orderBy(SortParam sortParam) {
        boolean ascending = sortParam.getSortDirection() == SortDirection.ASC;
        switch (sortParam.getSortCriteria()) {
            case CREATED_AT -> orderByCreatedAt(ascending);
            case ACTUAL_WORK_PERIOD_END -> orderByActualWorkPeriodEnd(ascending);
            case ESTIMATED_WORK_PERIOD_START -> orderByEstimatedWorkPeriodStart(ascending);
            case PRICE -> orderByPrice(ascending);
        }
        return this;
    }

    public Query orderByCreatedAt(boolean ascending) {
        return (Query) addComparator(Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder())), ascending);
    }

    public Query orderByActualWorkPeriodEnd(boolean ascending) {
        return (Query) addComparator(Comparator.comparing((Order order) -> order.getActualWorkPeriod().getEnd(), Comparator.nullsLast(Comparator.naturalOrder())), ascending);
    }

    public Query orderByEstimatedWorkPeriodStart(boolean ascending) {
        return (Query) addComparator(Comparator.comparing((Order order) -> order.getEstimatedWorkPeriod().getStart(), Comparator.nullsLast(Comparator.naturalOrder())), ascending);
    }

    public Query orderByPrice(boolean ascending) {
        return (Query) addComparator(Comparator.comparing(Order::getPrice), ascending);
    }

    public Query filterByStatus(OrderStatus status) {
        return (Query) addPredicate(order -> order.getStatus() == status);
    }

    public Query filterByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return (Query) addPredicate(order -> !order.getCreatedAt().isBefore(start) && !order.getCreatedAt().isAfter(end));
    }

    public Query filterByDeletedAtBetween(LocalDateTime start, LocalDateTime end) {
        return (Query) addPredicate(order -> !order.getDeletedAt().isBefore(start) && !order.getDeletedAt().isAfter(end));
    }

    public Query filterByCancelledAtBetween(LocalDateTime start, LocalDateTime end) {
        return (Query) addPredicate(order -> !order.getCanceledAt().isBefore(start) && !order.getCanceledAt().isAfter(end));
    }

    Query ignoreDeletedEntities() {
        return (Query) addPredicate(order -> order.getStatus() != OrderStatus.DELETED);
    }

    public Query filterByStatusInPeriod(OrderStatus status, Period period) {
        switch (status) {
            case CREATED -> filterByCreatedAtBetween(period.getStart(), period.getEnd());
            case DELETED -> filterByDeletedAtBetween(period.getStart(), period.getEnd());
            case CANCELED -> filterByCancelledAtBetween(period.getStart(), period.getEnd());
        }
        return this;
    }

    public Query filterByGarageSpotNumber(int number) {
        return (Query) addPredicate(order -> order.getGarageSpot().getNumber() == number);
    }

    public Query filterByMaster(Master master) {
        return (Query) addPredicate(order -> order.getMaster().getId() == master.getId());
    }

    public Query filterByStatuses(OrderStatus... orderStatuses) {
        return (Query) addPredicate(order -> Arrays.stream(orderStatuses)
                .anyMatch(status -> status == order.getStatus()));
    }
}
