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

    @ConfigProperty(propertyName = "db.url")
    private String dbUrl;

    @ConfigProperty(propertyName = "db.user")
    private String dbUser;

    @ConfigProperty(propertyName = "db.password")
    private String dbPassword;

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

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }
}
