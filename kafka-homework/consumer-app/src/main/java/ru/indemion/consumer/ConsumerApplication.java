package ru.indemion.consumer;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.indemion.consumer.config.AppConfig;

import java.util.concurrent.CountDownLatch;

public class ConsumerApplication {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Получен сигнал завершения, закрываем контекст...");
            context.close();
            latch.countDown();
        }));

        System.out.println("Приложение запущено. Ожидание сигнала завершения...");
        latch.await();
    }
}
