package task93;

import java.util.Random;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Main {
    public static void main(String[] args) {
        final int MAX_BUFFER_SIZE = 10;
        BlockingDeque<Integer> buffer = new LinkedBlockingDeque<>(MAX_BUFFER_SIZE);
        Thread producer = new Thread(() -> {
            Random random = new Random();
            while (true) {
                synchronized (buffer) {
                    if (buffer.size() == MAX_BUFFER_SIZE) {
                        try {
                            buffer.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    int value = random.nextInt(100);
                    buffer.add(value);
                    System.out.println("Добавлено число: " + value + " | Размер буфера: " + buffer.size());

                    buffer.notifyAll();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            while (true) {
                synchronized (buffer) {
                    if (buffer.isEmpty()) {
                        try {
                            buffer.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    System.out.println("Извлечено число: " + buffer.pop() + " | Размер буфера: " + buffer.size());
                    buffer.notifyAll();
                }
            }
        });

        producer.start();
        consumer.start();
    }
}
