package ru.indemion.carservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = "ru.indemion.carservice")
public class AppConfig {
    @Value("${restrictions.garageSpot.removable}")
    private boolean garageSpotRemovable;

    @Value("${restrictions.order.shiftableEstimatedWorkPeriod}")
    private boolean orderShiftableEstimatedWorkPeriod;

    @Value("${restrictions.order.removable}")
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
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
