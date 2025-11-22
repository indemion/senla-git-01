package carservice3.controllers;

import carservice3.common.ScannerDecorator;
import carservice3.common.Period;
import carservice3.common.SortDirection;
import carservice3.common.Util;
import carservice3.models.garage.GarageSpot;
import carservice3.models.garage.InMemoryGarageSpotManager;
import carservice3.models.master.InMemoryMasterManager;
import carservice3.models.master.Master;
import carservice3.models.order.*;
import carservice3.views.GarageSpotView;
import carservice3.views.MasterView;
import carservice3.views.OrderView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class OrderController {
    private final InMemoryOrderManager orderManager = InMemoryOrderManager.instance();
    private final InMemoryMasterManager masterManager = InMemoryMasterManager.instance();
    private final InMemoryGarageSpotManager garageSpotManager = InMemoryGarageSpotManager.instance();
    private final OrderView orderView = new OrderView();
    private final MasterView masterView = new MasterView();
    private final GarageSpotView garageSpotView = new GarageSpotView();
    private final ScannerDecorator scanner = ScannerDecorator.instance();

    public void index() {
        orderView.index(orderManager.getOrders());
    }

    public void filteredIndex() {
        FilterCriteria filterCriteria = Util.chooseVariant("Выберите критерий фильтрации заказов: ",
                List.of(FilterCriteria.values()));
        switch (filterCriteria) {
            case STATUS -> {
                OrderStatus status = Util.chooseVariant("Выберите статус: ", List.of(OrderStatus.values()));
                orderView.index(orderManager.getOrdersFilteredByStatus(status));
            }
            case STATUS_IN_PERIOD -> {
                OrderStatus status = Util.chooseVariant("Выберите статус: ", List.of(OrderStatus.values()));
                Period period = new Period();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");
                System.out.print("Введите начало периода в формате (11.11.2011 10:00): ");
                period.setStart(LocalDateTime.parse(scanner.nextLine(), dateTimeFormatter));
                System.out.print("Введите конец периода в формате (11.11.2011 10:00): ");
                period.setEnd(LocalDateTime.parse(scanner.nextLine(), dateTimeFormatter));
                orderView.index(orderManager.getOrdersFilteredByStatusInPeriod(status, period));
            }
        }
    }

    public void sortedIndex() {
        orderView.index(orderManager.getOrdersSorted(new SortParam(
                Util.chooseVariant("Выберите критерий сортировки: ", List.of(SortCriteria.values())),
                Util.chooseVariant("Выберите направление сортировки: ", List.of(SortDirection.values())))));
    }

    public void create() {
        System.out.print("Введите предполагаемую длительность выполнения заказа в часах: ");
        int durationInHours = scanner.nextInt();
        Period closestFreePeriod = masterManager.getClosestFreePeriodWithDuration(Duration.ofHours(durationInHours));
        System.out.println("Предполагаемый период выполнения заказа: " + closestFreePeriod);
        System.out.println("Список мастеров доступных на этот период: ");
        List<Master> freeMasters = masterManager.getMastersFreeInPeriod(closestFreePeriod);
        if (freeMasters.isEmpty()) {
            System.out.println("Нет свободных мастеров на заданный период, попробуйте снова, указав другой период");
            return;
        }
        masterView.index(freeMasters);
        System.out.print("Введите id мастера: ");
        int masterId = scanner.nextInt();
        Optional<Master> optionalMaster = masterManager.findById(masterId);
        if (optionalMaster.isEmpty()) {
            System.out.println("Мастер с таким id не найден");
            return;
        }
        System.out.println("Список гаражных мест доступных на этот период: ");
        List<GarageSpot> freeGarageSpots = garageSpotManager.getFreeGarageSpotsInPeriod(closestFreePeriod);
        if (freeGarageSpots.isEmpty()) {
            System.out.println("Нет свободных гаражных мест на этот период, попробуйте снова");
            return;
        }
        garageSpotView.index(freeGarageSpots);
        System.out.print("Введите номер гаражного места: ");
        int garageSpotNumber = scanner.nextInt();
        Optional<GarageSpot> optionalGarageSpot = garageSpotManager.findByNumber(garageSpotNumber);
        if (optionalGarageSpot.isEmpty()) {
            System.out.println("Место в гараже с таким номером не найдено");
            return;
        }
        System.out.print("Введите цену заказа: ");
        int price = scanner.nextInt();
        Order order = orderManager.create(price, optionalMaster.get(), optionalGarageSpot.get(),
                closestFreePeriod.getStart(), closestFreePeriod.getEnd());
        orderView.show(order);
    }

    public void delete() {
        System.out.print("Введите id заказа, который хотитет удалить: ");
        int id = scanner.nextInt();
        orderManager.remove(id);
        orderView.index(orderManager.getOrders());
    }

    public void startWorking() {
        System.out.print("Введите id заказа, над которым хотите начать работу: ");
        int id = scanner.nextInt();
        orderManager.startWorking(id);
        orderView.index(orderManager.getOrdersFilteredByStatus(OrderStatus.WORK_IN_PROGRESS));
    }

    public void close() {
        System.out.print("Введите id заказа, который хотите закрыть: ");
        int id = scanner.nextInt();
        orderManager.closeOrder(id);
        orderView.index(orderManager.getOrdersFilteredByStatus(OrderStatus.CLOSED));
    }

    public void cancel() {
        System.out.print("Введите id заказа, который хотите отменить: ");
        int id = scanner.nextInt();
        orderManager.cancelOrder(id);
        orderView.index(orderManager.getOrdersFilteredByStatus(OrderStatus.CANCELED));
    }

    public void shiftOrdersEstimatedWorkPeriod() {
        System.out.print("Введите кол-во часов на которое необходимо сместить все заказы: ");
        orderManager.shiftEstimatedWorkPeriodInCreatedOrders(Duration.ofHours(scanner.nextInt()));
        orderView.index(orderManager.getOrdersFilteredByStatus(OrderStatus.CREATED));
    }
}
