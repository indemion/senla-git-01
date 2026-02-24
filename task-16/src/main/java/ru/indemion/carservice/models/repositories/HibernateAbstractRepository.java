package ru.indemion.carservice.models.repositories;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.indemion.carservice.models.IHasId;

import java.util.List;
import java.util.Optional;

public class HibernateAbstractRepository<T extends IHasId> implements Repository<T> {
    protected final SessionFactory sessionFactory;
    protected final Class<T> entityClass;

    public HibernateAbstractRepository(SessionFactory sessionFactory, Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    public Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public T save(T entity) {
        if (entity.getId() == 0) {
            getCurrentSession().persist(entity);
            getCurrentSession().flush();
        } else {
            getCurrentSession().merge(entity);
        }
        return entity;
    }

    public void save(List<T> entity) {
        entity.forEach(this::save);
    }

    public void delete(T entity) {
        getCurrentSession().remove(entity);
    }

    public Optional<T> findById(int id) {
        T entity = getCurrentSession().find(entityClass, id);
        return entity != null ? Optional.of(entity) : Optional.empty();
    }

    public List<T> findAll() {
        return getCurrentSession().createQuery("FROM " + entityClass.getName(), entityClass).getResultList();
    }
}
