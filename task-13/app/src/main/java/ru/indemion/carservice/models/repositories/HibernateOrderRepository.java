package ru.indemion.carservice.models.repositories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import ru.indemion.carservice.models.order.FilterParams;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.order.SortParams;
import ru.indemion.carservice.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

public class HibernateOrderRepository extends HibernateAbstractRepository<Order> implements OrderRepository {
    public HibernateOrderRepository() {
        super(HibernateUtil.getCurrentSession(), Order.class);
    }

    @Override
    public List<Order> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(entityClass);
        Root<Order> orderRoot = cq.from(entityClass);
        cq.select(orderRoot);

        if (filterParams != null) {
            List<Predicate> predicateList = new ArrayList<>();
            if (filterParams.getMasterId() != null) {
                predicateList.add(cb.equal(orderRoot.get("master").get("id"), filterParams.getMasterId()));
            }
            if (filterParams.getStatuses() != null) {
                predicateList.add(orderRoot.get("status").in(filterParams.getStatuses()));
            }
            if (filterParams.getEstimatedWorkStartInPeriod() != null) {
                predicateList.add(cb.between(orderRoot.get("estimatedWorkPeriod").get("start"),
                        filterParams.getEstimatedWorkStartInPeriod().getStart(),
                        filterParams.getEstimatedWorkStartInPeriod().getEnd()));
            }
            if (!predicateList.isEmpty()) {
                cq.where(predicateList);
            }
        }

        if (sortParams != null) {
            List<jakarta.persistence.criteria.Order> orderList = new ArrayList<>();
            List<Path<?>> pathList = new ArrayList<>();
            switch (sortParams.getSortCriteria()) {
                case CREATED_AT -> pathList.add(orderRoot.get("createdAt"));
                case ACTUAL_WORK_PERIOD_END -> pathList.add(orderRoot.get("actualWorkPeriod").get("end"));
                case ESTIMATED_WORK_PERIOD_START -> pathList.add(orderRoot.get("estimatedWorkPeriod").get("start"));
                case PRICE -> pathList.add(orderRoot.get("price"));
            }
            switch (sortParams.getSortDirection()) {
                case ASC -> pathList.forEach(path -> orderList.add(cb.asc(path)));
                case DESC -> pathList.forEach(path -> orderList.add(cb.desc(path)));
            }
            cq.orderBy(orderList);
        }

        return session.createQuery(cq).getResultList();
    }
}
