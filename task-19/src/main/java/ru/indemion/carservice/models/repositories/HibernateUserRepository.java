package ru.indemion.carservice.models.repositories;

import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import ru.indemion.carservice.models.auth.User;

import java.util.Optional;

@Repository
public class HibernateUserRepository extends HibernateAbstractRepository<User> implements UserRepository {
    public HibernateUserRepository(SessionFactory sessionFactory) {
        super(sessionFactory, User.class);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        CriteriaBuilder cb = getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(entityClass);
        Root<User> user = cq.from(entityClass);
        cq.select(user).where(cb.equal(user.get("username"), username));
        try {
            return Optional.of(getCurrentSession().createQuery(cq).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
