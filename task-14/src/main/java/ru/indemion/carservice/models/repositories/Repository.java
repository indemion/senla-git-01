package ru.indemion.carservice.models.repositories;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    T save(T model);

    void save(List<T> models);

    void delete(T model);

    Optional<T> findById(int id);

    List<T> findAll();
}
