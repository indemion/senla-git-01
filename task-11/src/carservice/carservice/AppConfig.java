package carservice;

import carservice.annotations.ConfigProperty;

public class AppConfig {
    private static AppConfig instance;

    @ConfigProperty
    private String saveFile;

    @ConfigProperty(propertyName = "AppConfig.garageSpot.removable")
    private boolean garageSpotRemovable;

    @ConfigProperty(propertyName = "AppConfig.order.shiftableEstimatedWorkPeriod")
    private boolean orderShiftableEstimatedWorkPeriod;

    @ConfigProperty(propertyName = "AppConfig.order.removable")
    private boolean orderRemovable;

    @ConfigProperty(propertyName = "AppConfig.db.url")
    private String dbUrl;

    @ConfigProperty(propertyName = "AppConfig.db.user")
    private String dbUser;

    @ConfigProperty(propertyName = "AppConfig.db.password")
    private String dbPassword;

    public AppConfig() {}

    public String getSaveFile() {
        return saveFile;
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
