package carservice.common;

import carservice.AppConfig;
import carservice.models.garage.GarageSpotRepository;
import carservice.models.master.MasterRepository;
import carservice.models.order.OrderRepository;

public class ExitProcess implements Runnable {
    @Override
    public void run() {
        System.out.println("Получена команда выхода из приложения. Запущен процесс сохранения данных.");
        SerializationManager serializationManager = new SerializationManager();
        SerializationContainer serializationContainer = new SerializationContainer(OrderRepository.instance(),
                MasterRepository.instance(), GarageSpotRepository.instance());
        serializationManager.serialize(AppConfig.instance().getSaveFile(), serializationContainer);
        System.out.println("Данные сохранены. Завершение.");
    }
}
