package ru.indemion.carservice;

import ru.indemion.carservice.annotations.ConfigProperty;

public class AppConfig {
    private static AppConfig instance;

    @ConfigProperty(propertyName = "garageSpot.removable")
    private boolean garageSpotRemovable;

    @ConfigProperty(propertyName = "order.shiftableEstimatedWorkPeriod")
    private boolean orderShiftableEstimatedWorkPeriod;

    @ConfigProperty(propertyName = "order.removable")
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
