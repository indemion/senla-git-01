package ru.indemion.carservice.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.order.Order;

public class HibernateUtil {
    private static final SessionFactory sessionFactory;
    private static Session session;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml")
                    .addAnnotatedClass(Order.class)
                    .addAnnotatedClass(GarageSpot.class)
                    .addAnnotatedClass(Master.class);

            StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getCurrentSession() {
        if (session != null && session.isOpen()) {
            return session;
        }

        session = sessionFactory.openSession();
        return session;
    }
}
