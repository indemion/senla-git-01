package ru.indemion.consumer.repository;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import ru.indemion.consumer.model.Transfer;

@Repository
public class TransferRepository {
    private final EntityManager entityManager;

    public TransferRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void create(Transfer transfer) {
        entityManager.persist(transfer);
    }
}
