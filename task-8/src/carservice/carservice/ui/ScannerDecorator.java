package carservice.ui;

import java.util.Scanner;

public class ScannerDecorator {
    private static ScannerDecorator instance;
    private Scanner scanner;

    private ScannerDecorator() {}

    public static ScannerDecorator instance() {
        if (instance == null) {
            instance = new ScannerDecorator();
        }

        return instance;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    public String nextLine() {
        return scanner.nextLine();
    }

    public int nextInt() {
        return Integer.parseInt(scanner.nextLine());
    }
}
