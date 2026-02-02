package ru.indemion.carservice.models.services;

@FunctionalInterface
public interface TransactionalRunnable {
    void run() throws Exception;
}
