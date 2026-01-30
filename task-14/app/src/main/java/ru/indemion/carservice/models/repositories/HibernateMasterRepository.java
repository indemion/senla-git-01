package ru.indemion.carservice.models.repositories;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.master.FilterParams;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.SortParams;
import ru.indemion.carservice.util.HibernateUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HibernateMasterRepository extends HibernateAbstractRepository<Master> implements MasterRepository {
    public HibernateMasterRepository() {
        super(HibernateUtil.getCurrentSession(), Master.class);
    }

    @Override
    public List<Master> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Master> cq = cb.createQuery(entityClass);
        Root<Master> masterRoot = cq.from(entityClass);
        cq.select(masterRoot);

        if (filterParams != null) {
            if (filterParams.excludedIds() != null) {
                cq.where(cb.not(masterRoot.get("id").in(filterParams.excludedIds())));
            }
        }

        if (sortParams != null) {
            List<Order> orderList = new ArrayList<>();
            List<Path<?>> pathList = new ArrayList<>();
            switch (sortParams.getSortCriteria()) {
                case FULLNAME -> {
                    pathList.add(masterRoot.get("firstname"));
                    pathList.add(masterRoot.get("lastname"));
                }
                case STATUS -> pathList.add(masterRoot.get("status"));
            }
            switch (sortParams.getSortDirection()) {
                case ASC -> pathList.forEach(path -> orderList.add(cb.asc(path)));
                case DESC -> pathList.forEach(path -> orderList.add(cb.desc(path)));
            }
            cq.orderBy(orderList);
        }

        return session.createQuery(cq).getResultList();
    }

    @Override
    public List<Master> findSorted(SortParams sortParams) {
        return findFilteredAndSorted(null, sortParams);
    }

    @Override
    public List<Master> findMastersFreeInPeriod(Period period) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Master> cq = cb.createQuery(entityClass);
        Root<Master> masterRoot = cq.from(entityClass);

        Subquery<ru.indemion.carservice.models.order.Order> orderSubquery = cq.subquery(ru.indemion.carservice.models.order.Order.class);
        Root<ru.indemion.carservice.models.order.Order> orderRoot = orderSubquery.from(ru.indemion.carservice.models.order.Order.class);
        orderSubquery.select(orderRoot)
                .where(
                        cb.equal(orderRoot.get("master"), masterRoot),
                        cb.lessThan(orderRoot.get("estimatedWorkPeriod").get("start"), period.getStart()),
                        cb.greaterThan(orderRoot.get("estimatedWorkPeriod").get("end"), period.getEnd())
                );
        cq.select(masterRoot).where(cb.not(cb.exists(orderSubquery)));

        return session.createQuery(cq).getResultList();
    }

    @Override
    public Optional<Master> findByOrderId(int orderId) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Master> cq = cb.createQuery(entityClass);
        Root<Master> masterRoot = cq.from(entityClass);
        Join<Master, ru.indemion.carservice.models.order.Order> orderJoin = masterRoot.join("orders", JoinType.INNER);
        cq.where(cb.equal(orderJoin.get("id"), orderId));

        return Optional.ofNullable(session.createQuery(cq).getSingleResult());
    }
}
