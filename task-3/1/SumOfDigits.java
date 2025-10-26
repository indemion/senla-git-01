// Вариант 4: вывести на экран случайно сгенерированное трёхзначное натуральное число и сумму его цифр.
import java.util.Random;

public class SumOfDigits {
    private static final int MIN_VALUE = 100;
    private static final int MAX_VALUE = 999;

    public static void main(String[] args) {
        Random random = new Random();
//        int originalRandomNumber = random.nextInt(MIN_VALUE, MAX_VALUE + 1);
        // Так и не понял зачем использовать такую конструкцию в задании, если есть указанная выше, где сразу можно
        // задавать интервал для случайного числа.
        int originalRandomNumber = random.nextInt(900) + 100;

        int number = originalRandomNumber;
        int sum = 0;
        while (number != 0) {
            sum += number % 10;
            number /= 10;
        }

        System.out.println(String.format("Original number = %d and the sum of its digits = %d", originalRandomNumber, sum));
    }
}