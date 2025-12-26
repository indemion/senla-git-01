package task92;

import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int THREADS_QUANTITY = 2;
    private static final int MAX_PRINTS = 4;
    private static final List<Thread> threads = new ArrayList<>();
    private static final ThreadIndexer threadIndexer = new ThreadIndexer(0, THREADS_QUANTITY);

    public static void main(String[] args) {
        for (int i = 0; i < THREADS_QUANTITY; i++) {
            threads.add(new Thread(() -> {
                Thread currentThread = Thread.currentThread();
                int printsCount = 0;
                while (printsCount < MAX_PRINTS) {
                    if (currentThread.equals(threads.get(threadIndexer.getIndex()))) {
                        synchronized (threadIndexer) {
                            System.out.println(currentThread.getName());
                            threadIndexer.nextIndex();
                            printsCount++;
                        }
                    }
                }
            }));
        }
        threads.forEach(Thread::start);
    }

    private static class ThreadIndexer {
        private final int maxIndex;
        private int index;

        public ThreadIndexer(int startIndex, int maxIndex) {
            if (startIndex < 0 || maxIndex < 0 || startIndex > maxIndex) {
                String message;
                if (startIndex < 0) {
                    message = "startIndex не может быть меньше ноля";
                } else if (maxIndex < 0) {
                    message = "MaxIndex не может быть меньше ноля";
                } else {
                    message = "startIndex не может быть больше чем maxIndex";
                }
                throw new IllegalArgumentException(message);
            }
            this.index = startIndex;
            this.maxIndex = maxIndex;
        }

        public int getIndex() {
            return index;
        }

        public void nextIndex() {
            index = (index + 1) < maxIndex ? index + 1 : 0;
        }
    }
}
