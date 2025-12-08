package carservice6.ui.controllers;

import carservice6.AppConfig;
import carservice6.common.OperationProhibitedMessages;
import carservice6.common.Period;
import carservice6.common.SortDirection;
import carservice6.exceptions.OperationProhibitedException;
import carservice6.models.garage.GarageSpot;
import carservice6.models.garage.GarageSpotService;
import carservice6.models.master.Master;
import carservice6.models.master.MasterService;
import carservice6.models.order.*;
import carservice6.ui.ScannerDecorator;
import carservice6.ui.Util;
import carservice6.ui.views.GarageSpotView;
import carservice6.ui.views.MasterView;
import carservice6.ui.views.OrderView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class OrderController {
    private final OrderService orderService = OrderService.instance();
    private final MasterService masterService = MasterService.instance();
    private final GarageSpotService garageSpotService = GarageSpotService.instance();
    private final OrderView orderView = new OrderView();
    private final MasterView masterView = new MasterView();
    private final GarageSpotView garageSpotView = new GarageSpotView();
    private final ScannerDecorator scanner = ScannerDecorator.instance();

    public void index() {
        orderView.index(orderService.getOrders());
    }

    public void filteredIndex() {
        FilterCriteria filterCriteria = Util.chooseVariant("Выберите критерий фильтрации заказов: ",
                List.of(FilterCriteria.values()));
        switch (filterCriteria) {
            case STATUS -> {
                OrderStatus status = Util.chooseVariant("Выберите статус: ", List.of(OrderStatus.values()));
                orderView.index(orderService.getOrdersFilteredByStatus(status));
            }
            case STATUS_IN_PERIOD -> {
                OrderStatus status = Util.chooseVariant("Выберите статус: ", List.of(OrderStatus.values()));
                Period period = new Period();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d.M.yyyy H:mm");
                System.out.print("Введите начало периода в формате (11.11.2011 10:00): ");
                period.setStart(LocalDateTime.parse(scanner.nextLine(), dateTimeFormatter));
                System.out.print("Введите конец периода в формате (11.11.2011 10:00): ");
                period.setEnd(LocalDateTime.parse(scanner.nextLine(), dateTimeFormatter));
                orderView.index(orderService.getOrdersFilteredByStatusInPeriod(status, period));
            }
        }
    }

    public void sortedIndex() {
        orderView.index(orderService.getOrdersSorted(new SortParam(
                Util.chooseVariant("Выберите критерий сортировки: ", List.of(SortCriteria.values())),
                Util.chooseVariant("Выберите направление сортировки: ", List.of(SortDirection.values())))));
    }

    public void create() {
        System.out.print("Введите предполагаемую длительность выполнения заказа в часах: ");
        int durationInHours = scanner.nextInt();
        Period closestFreePeriod = masterService.getClosestFreePeriodWithDuration(Duration.ofHours(durationInHours));
        System.out.println("Предполагаемый период выполнения заказа: " + closestFreePeriod);
        System.out.println("Список мастеров доступных на этот период: ");
        List<Master> freeMasters = masterService.getMastersFreeInPeriod(closestFreePeriod);
        if (freeMasters.isEmpty()) {
            System.out.println("Нет свободных мастеров на заданный период, попробуйте снова, указав другой период");
            return;
        }
        masterView.index(freeMasters);
        System.out.print("Введите id мастера: ");
        int masterId = scanner.nextInt();
        Optional<Master> optionalMaster = masterService.findById(masterId);
        if (optionalMaster.isEmpty()) {
            System.out.println("Мастер с таким id не найден");
            return;
        }
        System.out.println("Список гаражных мест доступных на этот период: ");
        List<GarageSpot> freeGarageSpots = garageSpotService.getFreeGarageSpotsInPeriod(closestFreePeriod);
        if (freeGarageSpots.isEmpty()) {
            System.out.println("Нет свободных гаражных мест на этот период, попробуйте снова");
            return;
        }
        garageSpotView.index(freeGarageSpots);
        System.out.print("Введите номер гаражного места: ");
        int garageSpotNumber = scanner.nextInt();
        Optional<GarageSpot> optionalGarageSpot = garageSpotService.findByNumber(garageSpotNumber);
        if (optionalGarageSpot.isEmpty()) {
            System.out.println("Место в гараже с таким номером не найдено");
            return;
        }
        System.out.print("Введите цену заказа: ");
        int price = scanner.nextInt();
        Order order = orderService.create(price, optionalMaster.get(), optionalGarageSpot.get(),
                closestFreePeriod.getStart(), closestFreePeriod.getEnd());
        orderView.show(order);
    }

    public void delete() {
        if (!AppConfig.instance().isOrderRemovable()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_REMOVING);
        }
        System.out.print("Введите id заказа, который хотитет удалить: ");
        int id = scanner.nextInt();
        orderService.softDelete(id);
        orderView.index(orderService.getOrders());
    }

    public void startWorking() {
        System.out.print("Введите id заказа, над которым хотите начать работу: ");
        int id = scanner.nextInt();
        orderService.startWorking(id);
        orderView.index(orderService.getOrdersFilteredByStatus(OrderStatus.WORK_IN_PROGRESS));
    }

    public void close() {
        System.out.print("Введите id заказа, который хотите закрыть: ");
        int id = scanner.nextInt();
        orderService.closeOrder(id);
        orderView.index(orderService.getOrdersFilteredByStatus(OrderStatus.CLOSED));
    }

    public void cancel() {
        System.out.print("Введите id заказа, который хотите отменить: ");
        int id = scanner.nextInt();
        orderService.cancelOrder(id);
        orderView.index(orderService.getOrdersFilteredByStatus(OrderStatus.CANCELED));
    }

    public void shiftOrdersEstimatedWorkPeriod() {
        if (!AppConfig.instance().isOrderShiftableEstimatedWorkPeriod()) {
            throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_SHIFTING_ESTIMATED_WORK_PERIOD);
        }
        System.out.print("Введите кол-во часов на которое необходимо сместить все заказы: ");
        orderService.shiftEstimatedWorkPeriodInCreatedOrders(Duration.ofHours(scanner.nextInt()));
        orderView.index(orderService.getOrdersFilteredByStatus(OrderStatus.CREATED));
    }

    public void exportToPath() {
        String defaultPath = (new CsvExporter()).getDefaultPath();
        System.out.print("Введите путь к файлу в который хотите экспортировать сущности (по умолчанию " + defaultPath + "): ");
        String path = orderService.exportToPath(scanner.nextLine());
        System.out.println("Создан файл " + path);
    }

    public void importFromPath() {
        System.out.print("Введите путь к файлу из которого вы хотите импортировать сущности: ");
        orderService.importFromPath(scanner.nextLine());
    }
}
