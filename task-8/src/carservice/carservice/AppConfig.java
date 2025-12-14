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

    public AppConfig() {}

    public static AppConfig instance() {
        if (instance == null) {
            instance = new AppConfig();
        }

        return instance;
    }

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
}
