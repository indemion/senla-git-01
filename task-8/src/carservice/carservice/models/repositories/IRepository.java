package carservice.models.repositories;

import carservice.models.IHasId;

import java.util.List;
import java.util.Optional;

public interface IRepository<T extends IHasId> {
    void save(T entity);

    void save(List<T> entities);

    void delete(int id);

    Optional<T> findById(int id);

    List<T> findAll();
}
