package task91;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Object commonObject = new Object();
        Thread thread = new Thread(() -> {
            // Thread.State.RUNNABLE
            System.out.println(Thread.currentThread().getState());
            synchronized (commonObject) {
                try {
                    commonObject.wait();
                    commonObject.wait(2000);
                    while (!Thread.currentThread().isInterrupted()) {

                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        // Thread.State.NEW
        System.out.println(thread.getState());
        thread.start();
        synchronized (commonObject) {
            Thread.sleep(1000);
            // Thread.State.BLOCKED
            System.out.println(thread.getState());
        }
        Thread.sleep(1000);
        // Thread.State.WAITING
        System.out.println(thread.getState());
        synchronized (commonObject) {
            commonObject.notify();
        }
        Thread.sleep(1000);
        // Thread.State.TIMED_WAITING
        System.out.println(thread.getState());
        Thread.sleep(1000);
        thread.interrupt();
        Thread.sleep(1000);
        // Thread.State.TERMINATED
        System.out.println(thread.getState());
    }
}