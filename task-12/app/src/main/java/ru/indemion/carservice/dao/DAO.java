package ru.indemion.carservice.dao;

import java.util.List;

public interface DAO<T> {
    T insert(T entity);

    int update(T entity);

    T save(T entity);

    int delete(int id);

    T findById(int id);

    List<T> findAll();
}
