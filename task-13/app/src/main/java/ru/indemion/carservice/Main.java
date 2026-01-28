package ru.indemion.carservice;

import ru.indemion.carservice.annotations.Configurator;
import ru.indemion.carservice.models.repositories.GarageSpotRepository;
import ru.indemion.carservice.models.repositories.HibernateGarageSpotRepository;
import ru.indemion.carservice.models.repositories.HibernateMasterRepository;
import ru.indemion.carservice.models.repositories.HibernateOrderRepository;
import ru.indemion.carservice.models.repositories.MasterRepository;
import ru.indemion.carservice.models.repositories.OrderRepository;
import ru.indemion.di.Container;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        AppConfig appConfig = new AppConfig();
        Configurator configurator = new Configurator();
        configurator.configure(appConfig);
        Container.INSTANCE.register(MasterRepository.class, HibernateMasterRepository.class);
        Container.INSTANCE.register(GarageSpotRepository.class, HibernateGarageSpotRepository.class);
        Container.INSTANCE.register(OrderRepository.class, HibernateOrderRepository.class);
        Container.INSTANCE.registerInstance(AppConfig.class, appConfig);
        App app = Container.INSTANCE.resolve(App.class);
        app.run();
    }
}