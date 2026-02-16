package ru.indemion.carservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:app.properties")
@ComponentScan(basePackages = "ru.indemion.carservice")
public class AppConfig {
    @Value("${garageSpot.removable}")
    private boolean garageSpotRemovable;

    @Value("${order.shiftableEstimatedWorkPeriod}")
    private boolean orderShiftableEstimatedWorkPeriod;

    @Value("${order.removable}")
    private boolean orderRemovable;

    public AppConfig() {
    }

    public boolean isGarageSpotRemovable() {
        return garageSpotRemovable;
    }

    public boolean isOrderShiftableEstimatedWorkPeriod() {
        return orderShiftableEstimatedWorkPeriod;
    }

    public boolean isOrderRemovable() {
        return orderRemovable;
    }

    @Bean
    public DataSource dataSource() {
        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration()
                .configure("hibernate.cfg.xml");
        Properties props = configuration.getProperties();

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(props.getProperty("hibernate.connection.url"));
        dataSource.setUsername(props.getProperty("hibernate.connection.username"));
        dataSource.setPassword(props.getProperty("hibernate.connection.password"));
        return dataSource;
    }
}
