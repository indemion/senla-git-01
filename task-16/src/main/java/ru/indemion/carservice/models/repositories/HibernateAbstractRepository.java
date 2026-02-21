package ru.indemion.carservice.models.repositories;

import org.hibernate.Session;
import ru.indemion.carservice.models.IHasId;

import java.util.List;
import java.util.Optional;

public class HibernateAbstractRepository<T extends IHasId> implements Repository<T> {
    protected final Session session;
    protected final Class<T> entityClass;

    public HibernateAbstractRepository(Session session, Class<T> entityClass) {
        this.session = session;
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        if (entity.getId() == 0) {
            session.persist(entity);
            session.flush();
        } else {
            session.merge(entity);
        }
        return entity;
    }

    public void save(List<T> entity) {
        entity.forEach(this::save);
    }

    public void delete(T entity) {
        session.remove(entity);
    }

    public Optional<T> findById(int id) {
        T entity = session.find(entityClass, id);
        return entity != null ? Optional.of(entity) : Optional.empty();
    }

    public List<T> findAll() {
        return session.createQuery("FROM " + entityClass.getName(), entityClass).getResultList();
    }
}
