package carservice4.ui;

import java.util.Scanner;

public class ScannerDecorator {
    private static ScannerDecorator instance;
    private final Scanner scanner = new Scanner(System.in);

    private ScannerDecorator() {
    }

    public static ScannerDecorator instance() {
        if (instance == null) {
            instance = new ScannerDecorator();
        }

        return instance;
    }

    public String nextLine() {
        return scanner.nextLine();
    }

    public int nextInt() {
        return Integer.parseInt(scanner.nextLine());
    }
}
