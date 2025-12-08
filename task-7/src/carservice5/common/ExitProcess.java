package carservice5.common;

import carservice5.Config;
import carservice5.models.garage.GarageSpotRepository;
import carservice5.models.master.MasterRepository;
import carservice5.models.order.OrderRepository;

public class ExitProcess implements Runnable {
    @Override
    public void run() {
        System.out.println("Получена команда выхода из приложения. Запущен процесс сохранения данных.");
        SerializationManager serializationManager = new SerializationManager();
        SerializationContainer serializationContainer = new SerializationContainer(OrderRepository.instance(),
                MasterRepository.instance(), GarageSpotRepository.instance());
        serializationManager.serialize(Config.instance().getProperty("saveFile"), serializationContainer);
        System.out.println("Данные сохранены. Завершение.");
    }
}
