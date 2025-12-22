package carservice;

import carservice.annotations.Configurator;
import di.Container;

public class Main {
    public static void main(String[] args) {
        AppConfig appConfig = new AppConfig();
        Configurator configurator = new Configurator();
        configurator.configure(appConfig);
        Container.INSTANCE.registerInstance(AppConfig.class, appConfig);
        App app = Container.INSTANCE.resolve(App.class);
        app.run();
    }
}