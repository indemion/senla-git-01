package ru.indemion.carservice.models.services;

@FunctionalInterface
public interface TransactionalOperation<T> {
    T execute() throws Exception;
}
