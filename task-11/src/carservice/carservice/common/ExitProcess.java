package carservice.common;

public class ExitProcess extends Thread {
    @Override
    public void run() {
        System.out.println("Получена команда выхода из приложения. Запущен процесс сохранения данных.");
        System.out.println("Данные сохранены. Завершение.");
    }
}
