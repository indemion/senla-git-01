package carservice6.common;

import carservice6.AppConfig;
import carservice6.models.garage.GarageSpotRepository;
import carservice6.models.master.MasterRepository;
import carservice6.models.order.OrderRepository;

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
