package carservice6;

import carservice6.annotations.ConfigProperty;

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

    private AppConfig() {}

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
