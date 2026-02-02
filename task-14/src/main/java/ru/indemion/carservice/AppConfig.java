package ru.indemion.carservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

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
}
