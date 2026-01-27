package ru.indemion.carservice.models.repositories;

public interface Mapper<M, E> {
    M entityToModel(E entity);

    E modelToEntity(M model);
}
