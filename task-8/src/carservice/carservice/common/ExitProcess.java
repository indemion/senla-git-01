package carservice.common;

import carservice.models.garage.GarageSpotRepository;
import carservice.models.master.MasterRepository;
import carservice.models.order.OrderRepository;
import di.Inject;

public class ExitProcess extends Thread {
    private final SerializationManager serializationManager;
    private final OrderRepository orderRepository;
    private final MasterRepository masterRepository;
    private final GarageSpotRepository garageSpotRepository;

    @Inject
    public ExitProcess(SerializationManager serializationManager, OrderRepository orderRepository,
                       MasterRepository masterRepository, GarageSpotRepository garageSpotRepository) {
        this.serializationManager = serializationManager;
        this.orderRepository = orderRepository;
        this.masterRepository = masterRepository;
        this.garageSpotRepository = garageSpotRepository;
    }

    @Override
    public void run() {
        System.out.println("Получена команда выхода из приложения. Запущен процесс сохранения данных.");
        SerializationContainer serializationContainer = new SerializationContainer(orderRepository.findAll(),
                masterRepository.findAll(), garageSpotRepository.findAll());
        serializationManager.save(serializationContainer);
        System.out.println("Данные сохранены. Завершение.");
    }
}
