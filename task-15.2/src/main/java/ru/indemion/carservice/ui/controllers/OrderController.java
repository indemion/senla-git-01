package ru.indemion.carservice.ui.controllers;

import org.springframework.stereotype.Component;
import ru.indemion.carservice.AppConfig;
import ru.indemion.carservice.common.OperationProhibitedMessages;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.common.SortDirection;
import ru.indemion.carservice.exceptions.OperationProhibitedException;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotService;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.MasterService;
import ru.indemion.carservice.models.order.OrderCsvExporter;
import ru.indemion.carservice.models.order.FilterCriteria;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.order.OrderService;
import ru.indemion.carservice.models.order.OrderStatus;
import ru.indemion.carservice.models.order.SortCriteria;
import ru.indemion.carservice.models.order.SortParams;
import ru.indemion.carservice.models.services.FreePeriodService;
import ru.indemion.carservice.ui.utils.OperationLogger;
import ru.indemion.carservice.ui.utils.ScannerDecorator;
import ru.indemion.carservice.ui.utils.Util;
import ru.indemion.carservice.ui.views.GarageSpotView;
import ru.indemion.carservice.ui.views.MasterView;
import ru.indemion.carservice.ui.views.OrderView;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Component
public class OrderController {
    private final OrderService orderService;
    private final MasterService masterService;
    private final GarageSpotService garageSpotService;
    private final FreePeriodService freePeriodService;
    private final AppConfig appConfig;
    private final OperationLogger operationLogger;
    private final OrderView orderView = new OrderView();
    private final MasterView masterView = new MasterView();
    private final GarageSpotView garageSpotView = new GarageSpotView();
    private final ScannerDecorator scanner = ScannerDecorator.instance();

    public OrderController(OrderService orderService, MasterService masterService, GarageSpotService garageSpotService,
                           FreePeriodService freePeriodService, AppConfig appConfig, OperationLogger operationLogger) {
        this.orderService = orderService;
        this.masterService = masterService;
        this.garageSpotService = garageSpotService;
        this.freePeriodService = freePeriodService;
        this.appConfig = appConfig;
        this.operationLogger = operationLogger;
    }

    public void index() {
        operationLogger.log("получение списка заказов", () -> {
            orderView.index(orderService.getOrders());
        });
    }

    public void filteredIndex() {
        operationLogger.log("получение фильтрованного списка заказов", () -> {
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
        });
    }

    public void sortedIndex() {
        operationLogger.log("получение сортированного списка заказов", () -> {
            orderView.index(orderService.getOrdersSorted(new SortParams(
                    Util.chooseVariant("Выберите критерий сортировки: ", List.of(SortCriteria.values())),
                    Util.chooseVariant("Выберите направление сортировки: ", List.of(SortDirection.values())))));
        });
    }

    public void create() {
        operationLogger.log("создание заказа", () -> {
            System.out.print("Введите предполагаемую длительность выполнения заказа в часах: ");
            int durationInHours = scanner.nextInt();
            Period closestFreePeriod = freePeriodService.getClosestFreePeriodWithDuration(Duration.ofHours(durationInHours));
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
        });
    }

    public void delete() {
        operationLogger.log("удаление заказа", () -> {
            if (!appConfig.isOrderRemovable()) {
                throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_REMOVING);
            }
            System.out.print("Введите id заказа, который хотитет удалить: ");
            int id = scanner.nextInt();
            orderService.softDelete(id);
            orderView.index(orderService.getOrders());
        });
    }

    public void startWorking() {
        operationLogger.log("начать работу над заказом", () -> {
            System.out.print("Введите id заказа, над которым хотите начать работу: ");
            int id = scanner.nextInt();
            orderService.startWorking(id);
            orderView.index(orderService.getOrdersFilteredByStatus(OrderStatus.WORK_IN_PROGRESS));
        });
    }

    public void close() {
        operationLogger.log("закрыть заказ", () -> {
            System.out.print("Введите id заказа, который хотите закрыть: ");
            int id = scanner.nextInt();
            orderService.closeOrder(id);
            orderView.index(orderService.getOrdersFilteredByStatus(OrderStatus.CLOSED));
        });
    }

    public void cancel() {
        operationLogger.log("отменить заказ", () -> {
            System.out.print("Введите id заказа, который хотите отменить: ");
            int id = scanner.nextInt();
            orderService.cancelOrder(id);
            orderView.index(orderService.getOrdersFilteredByStatus(OrderStatus.CANCELED));
        });
    }

    public void shiftOrdersEstimatedWorkPeriod() {
        operationLogger.log("сместить время выполнения заказов", () -> {
            if (!appConfig.isOrderShiftableEstimatedWorkPeriod()) {
                throw new OperationProhibitedException(OperationProhibitedMessages.ORDER_SHIFTING_ESTIMATED_WORK_PERIOD);
            }
            System.out.print("Введите кол-во часов на которое необходимо сместить все заказы: ");
            orderService.shiftEstimatedWorkPeriodInCreatedOrders(Duration.ofHours(scanner.nextInt()));
            orderView.index(orderService.getOrdersFilteredByStatus(OrderStatus.CREATED));
        });
    }

    public void exportToPath() {
        operationLogger.log("экспорт заказов в файл", () -> {
            String defaultPath = (new OrderCsvExporter()).getDefaultPath();
            System.out.print("Введите путь к файлу в который хотите экспортировать сущности (по умолчанию " + defaultPath + "): ");
            String path = orderService.exportToPath(scanner.nextLine());
            System.out.println("Создан файл " + path);
        });
    }

    public void importFromPath() {
        operationLogger.log("импорт заказов из файла", () -> {
            System.out.print("Введите путь к файлу из которого вы хотите импортировать сущности: ");
            orderService.importFromPath(scanner.nextLine());
        });
    }
}
