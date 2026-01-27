package ru.indemion.carservice.models.services;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.indemion.carservice.exceptions.ServiceException;

public abstract class AbstractTransactionalService {
    protected Session session;

    public AbstractTransactionalService(Session session) {
        this.session = session;
    }

    protected <T> T inTransaction(TransactionalOperation<T> operation) {
        Transaction tx = session.getTransaction();
        try {
            tx.begin();
            T result = operation.execute();
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new ServiceException("Ошибка транзакции", e);
        }
    }

    protected void inTransaction(TransactionalRunnable operation) {
        inTransaction(() -> {
            operation.run();
            return null;
        });
    }
}