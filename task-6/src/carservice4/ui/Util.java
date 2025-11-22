package carservice4.ui;

import java.util.List;

public class Util {
    public static <T> T chooseVariant(String message, List<T> variants) {
        int idx = 0;
        for (T variant : variants) {
            System.out.printf("[%d] %s\n", idx, variant);
            idx++;
        }
        System.out.print(message);
        return variants.get(Integer.parseInt(ScannerDecorator.instance().nextLine()));
    }
}
