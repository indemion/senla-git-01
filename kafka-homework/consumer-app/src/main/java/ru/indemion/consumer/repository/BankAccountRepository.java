package ru.indemion.consumer.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.indemion.consumer.model.BankAccount;

@Repository
public class BankAccountRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(BankAccount bankAccount) {
        entityManager.merge(bankAccount);
    }

    public BankAccount findById(Integer id) {
        return entityManager.find(BankAccount.class, id);
    }
}
