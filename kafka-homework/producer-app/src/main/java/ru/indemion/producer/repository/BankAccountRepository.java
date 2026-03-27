package ru.indemion.producer.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.indemion.producer.model.BankAccount;

import java.util.List;

@Repository
public class BankAccountRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void save(BankAccount bankAccount) {
        if (bankAccount.getId() == null) {
            entityManager.persist(bankAccount);
        } else {
            entityManager.merge(bankAccount);
        }
    }

    public List<BankAccount> findAll() {
        return entityManager.createQuery("SELECT a FROM BankAccount a", BankAccount.class).getResultList();
    }

    public Long count() {
        return entityManager.createQuery("SELECT COUNT(a) FROM BankAccount a", Long.class).getSingleResult();
    }
}
