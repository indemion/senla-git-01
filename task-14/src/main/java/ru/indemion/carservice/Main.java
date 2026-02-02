package ru.indemion.carservice;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.indemion.carservice.ui.controllers.ConsoleController;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ConsoleController controller = context.getBean(ConsoleController.class);
        controller.run();
    }
}