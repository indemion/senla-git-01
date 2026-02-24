package ru.indemion.carservice.models.repositories;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.common.SortParams;
import ru.indemion.carservice.models.garage.FilterParams;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotStatus;
import ru.indemion.carservice.models.garage.SortCriteria;
import ru.indemion.carservice.models.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HibernateGarageSpotRepository extends HibernateAbstractRepository<GarageSpot>
        implements GarageSpotRepository {
    public HibernateGarageSpotRepository(SessionFactory sessionFactory) {
        super(sessionFactory, GarageSpot.class);
    }

    @Override
    public Optional<GarageSpot> findByNumber(int number) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<GarageSpot> cq = cb.createQuery(entityClass);
        Root<GarageSpot> garageSpotRoot = cq.from(entityClass);
        cq.select(garageSpotRoot).where(cb.equal(garageSpotRoot.get("number"), number));
        try {
            return Optional.of(getCurrentSession().createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<GarageSpot> findFreeGarageSpotsInPeriod(Period period) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<GarageSpot> cq = cb.createQuery(entityClass);
        Root<GarageSpot> garageSpotRoot = cq.from(entityClass);

        Subquery<Order> orderSubquery = cq.subquery(Order.class);
        Root<Order> orderRoot = orderSubquery.from(Order.class);
        orderSubquery.select(orderRoot)
                .where(
                    cb.equal(orderRoot.get("garageSpot"), garageSpotRoot),
                    cb.lessThanOrEqualTo(orderRoot.get("estimatedWorkPeriod").get("start"), period.getStart()),
                    cb.greaterThanOrEqualTo(orderRoot.get("estimatedWorkPeriod").get("end"), period.getEnd())
                );
        cq.select(garageSpotRoot).where(cb.not(cb.exists(orderSubquery)));

        return getCurrentSession().createQuery(cq).getResultList();
    }

    @Override
    public List<GarageSpot> findFilteredByStatus(GarageSpotStatus status) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<GarageSpot> cq = cb.createQuery(entityClass);
        Root<GarageSpot> garageSpotRoot = cq.from(entityClass);
        cq.select(garageSpotRoot).where(cb.equal(garageSpotRoot.get("status"), status));

        return getCurrentSession().createQuery(cq).getResultList();
    }

    @Override
    public List<GarageSpot> findFilteredAndSorted(FilterParams filterParams, SortParams<SortCriteria> sortParams) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<GarageSpot> cq = cb.createQuery(entityClass);
        Root<GarageSpot> garageSpotRoot = cq.from(entityClass);
        cq.select(garageSpotRoot);

        if (filterParams != null) {
            if (filterParams.status() != null) {
                cq.where(cb.equal(garageSpotRoot.get("status"), filterParams.status()));
            }
        }

        if (sortParams != null) {
            List<jakarta.persistence.criteria.Order> orderList = new ArrayList<>();
            List<Path<?>> pathList = new ArrayList<>();
            switch (sortParams.getSortCriteria()) {
                case ID -> {
                    pathList.add(garageSpotRoot.get("id"));
                }
                case NUMBER -> {
                    pathList.add(garageSpotRoot.get("number"));
                }
                case STATUS -> pathList.add(garageSpotRoot.get("status"));
            }
            switch (sortParams.getSortDirection()) {
                case ASC -> pathList.forEach(path -> orderList.add(cb.asc(path)));
                case DESC -> pathList.forEach(path -> orderList.add(cb.desc(path)));
            }
            cq.orderBy(orderList);
        }

        return getCurrentSession().createQuery(cq).getResultList();
    }
}
