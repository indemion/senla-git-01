package carservice;

import carservice.annotations.Configurator;
import carservice.dao.*;
import carservice.models.repositories.*;
import di.Container;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        AppConfig appConfig = new AppConfig();
        Configurator configurator = new Configurator();
        configurator.configure(appConfig);
        Container.INSTANCE.register(MasterRepository.class, DBMasterRepository.class);
        Container.INSTANCE.register(MasterDAO.class, MasterDAOImpl.class);
        Container.INSTANCE.register(GarageSpotRepository.class, DBGarageSpotRepository.class);
        Container.INSTANCE.register(GarageSpotDAO.class, GarageSpotDAOImpl.class);
        Container.INSTANCE.register(OrderRepository.class, DBOrderRepository.class);
        Container.INSTANCE.register(OrderDAO.class, OrderDAOImpl.class);
        Container.INSTANCE.registerInstance(AppConfig.class, appConfig);
        Container.INSTANCE.registerInstance(Connection.class, DriverManager.getConnection(appConfig.getDbUrl(),
                appConfig.getDbUser(), appConfig.getDbPassword()));
        App app = Container.INSTANCE.resolve(App.class);
        app.run();
    }
}