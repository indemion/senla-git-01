package ru.indemion.carservice.models.repositories;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotStatus;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.util.HibernateUtil;

import java.util.List;
import java.util.Optional;

public class HibernateGarageSpotRepository extends HibernateAbstractRepository<GarageSpot>
        implements GarageSpotRepository {
    public HibernateGarageSpotRepository() {
        super(HibernateUtil.getCurrentSession(), GarageSpot.class);
    }

    @Override
    public Optional<GarageSpot> findByNumber(int number) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GarageSpot> cq = cb.createQuery(entityClass);
        Root<GarageSpot> garageSpotRoot = cq.from(entityClass);
        cq.select(garageSpotRoot).where(cb.equal(garageSpotRoot.get("number"), number));
        try {
            return Optional.of(session.createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<GarageSpot> findFreeGarageSpotsInPeriod(Period period) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
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

        return session.createQuery(cq).getResultList();
    }

    @Override
    public List<GarageSpot> findFilteredByStatus(GarageSpotStatus status) {
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<GarageSpot> cq = cb.createQuery(entityClass);
        Root<GarageSpot> garageSpotRoot = cq.from(entityClass);
        cq.select(garageSpotRoot).where(cb.equal(garageSpotRoot.get("status"), status));

        return session.createQuery(cq).getResultList();
    }
}
