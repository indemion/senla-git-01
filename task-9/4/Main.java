package task94;

import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Thread daemon = new CustomThread(2);
        daemon.setDaemon(true);
        daemon.start();

        Thread.sleep(10 * 1000);
    }

    private static class CustomThread extends Thread {
        private final int delayInSeconds;

        private CustomThread(int delayInSeconds) {
            this.delayInSeconds = delayInSeconds;
        }

        @Override
        public void run() {
            while (true) {
                System.out.println(LocalDateTime.now());
                try {
                    Thread.sleep(delayInSeconds * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}