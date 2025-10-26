package carservice;

import carservice.garage.Garage;
import carservice.garage.GarageSpot;
import carservice.master.Master;
import carservice.master.MasterManager;
import carservice.order.Order;
import carservice.order.OrderManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

public class CarServiceTest {
    public static void main(String[] args) {
        Random random = new Random();
        Garage garage = new Garage();
        OrderManager orderManager = new OrderManager();
        MasterManager masterManager = new MasterManager();

        // Добавление, удаление мест в гараже
        for (int i = 0; i < 5; i++) {
            garage.createSpot(i + 1);
        }
        garage.removeSpot(5);
        garage.createSpot(5);

        // Добавление, удаление мастеров
        for (int i = 0; i < 2; i++) {
            masterManager.createMaster("Работник №"+(i+1), "+7000000000"+(i+1));
        }
        Master master = masterManager.createMaster("Работник на удаление", "+70000004545");
        masterManager.removeMaster(master.getId());

        // Добавление, удаление, закрытие и отмена заказов
        for (int i = 0; i < 3; i++) {
            orderManager.createOrder(LocalDateTime.now().plusHours(i+1), LocalDateTime.now().plusHours(i+2));
        }
        Order order = orderManager.getOrders().get(random.nextInt(orderManager.getOrders().size()));
        orderManager.startWorking(order);
        orderManager.shiftEstimatedWorkTimeWindowInCreatedOrders(Duration.ofHours(1));
        order = orderManager.getOrders().get(random.nextInt(orderManager.getOrders().size()));
        orderManager.closeOrder(order);
        orderManager.cancelOrder(order);
        orderManager.removeOrder(order.getId());
        orderManager.removeOrder(order.getId());
    }
}
