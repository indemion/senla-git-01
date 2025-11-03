package carservice2;

import carservice2.common.Period;
import carservice2.garage.GarageSpot;
import carservice2.garage.InMemoryGarageSpotManager;
import carservice2.master.InMemoryMasterManager;
import carservice2.master.Master;
import carservice2.master.MasterStatus;
import carservice2.master.SortCriteria;
import carservice2.order.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class CarServiceTest {
    private static final Random random = new Random();
    private static final InMemoryMasterManager masterManager = new InMemoryMasterManager();
    private static final InMemoryOrderManager orderManager = new InMemoryOrderManager();
    private static final InMemoryGarageSpotManager garageSpotManager = new InMemoryGarageSpotManager();

    static {
        garageSpotManager.setOrderManager(orderManager);
        masterManager.setOrderManager(orderManager);
    }

    public static void main(String[] args) throws InterruptedException {
        initManagers();

        System.out.println("Список свободных мест в гараже");
        printGarageSpots(garageSpotManager.getFreeGarageSpots());

        System.out.println("Список заказов:");
        System.out.println("сортированный по дате создания:");
        printOrders(orderManager.getOrdersSorted(new SortParam(SortField.CREATED_AT)));
        System.out.println("сортированный по дате выполнения:");
        // Сделаем пару первых созданных заказов выполненными
        List<Order> wipOrders = orderManager.getOrdersFilteredByStatus(OrderStatus.WORK_IN_PROGRESS);
        for (int i = 0; i < 2; i++) {
            orderManager.closeOrder(wipOrders.get(i));
            Thread.sleep(1000);
        }
        printOrders(orderManager.getOrdersSorted(new SortParam(SortField.ACTUAL_WORK_PERIOD_END)));
        System.out.println("сортированный по планируемой дате выполнения:");
        printOrders(orderManager.getOrdersSorted(new SortParam(SortField.ESTIMATED_WORK_PERIOD_START)));
        System.out.println("сортированный по цене:");
        printOrders(orderManager.getOrdersSorted(new SortParam(SortField.PRICE)));

        System.out.println("Список авто-мастеров:");
        System.out.println("сортированный по алфавиту:");
        printMasters(masterManager.getMastersSorted(new carservice2.master.SortParam(SortCriteria.FULLNAME)));
        System.out.println("сортированный по занятости:");
        printMasters(masterManager.getMastersSorted(new carservice2.master.SortParam(SortCriteria.STATUS)));

        System.out.println("Список текущих выполняемых заказов:");
        System.out.println("отсортированный по дате создания:");
        printOrders(orderManager.getWIPOrdersSorted(new SortParam(SortField.CREATED_AT)));
        System.out.println("отсортированный по дате выполнения:");
        printOrders(orderManager.getWIPOrdersSorted(new SortParam(SortField.ACTUAL_WORK_PERIOD_END)));
        System.out.println("отсортированный по цене:");
        printOrders(orderManager.getWIPOrdersSorted(new SortParam(SortField.PRICE)));

        Optional<Master> optionalMaster = masterManager.getMastersFilteredByStatus(MasterStatus.BUSY).stream().findFirst();
        if (optionalMaster.isPresent()) {
            System.out.println("Заказ выполняемый конкретным мастером:");
            printOrder(orderManager.getWIPOrderByMaster(optionalMaster.get()));
            printMaster(optionalMaster.get());
        }

        Optional<Order> optionalWIPOrder = orderManager.getWIPOrders().stream().findFirst();
        if (optionalWIPOrder.isPresent()) {
            System.out.println("Мастер привязанный к конкретному заказу");
            printMaster(masterManager.getMasterByOrder(optionalWIPOrder.get()));
            printOrder(optionalWIPOrder.get());
        }

        System.out.println("Список заказов завершённых в период и остортированный по цене:");
        printOrders(orderManager.getOrdersFilteredByStatusInPeriodSorted(OrderStatus.CLOSED,
                new Period(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1)),
                new SortParam(SortField.PRICE)));

        System.out.println("Количество свободных мест на дату:");
        LocalDate localDate = LocalDate.now().plusDays(5);
        // Запланируем заказ на ту же дату которую будем проверять
        optionalMaster = masterManager.findById(1);
        Optional<GarageSpot> optionalGarageSpot = garageSpotManager.getFreeGarageSpotsOnDate(localDate).stream()
                .findFirst();
        Period estimatedWorkPeriod = new Period(localDate.atStartOfDay(), localDate.atTime(LocalTime.MAX));
        orderManager.create(1000, optionalMaster.get(), optionalGarageSpot.get(), estimatedWorkPeriod.getStart(),
                estimatedWorkPeriod.getEnd());
        System.out.println(String.format("Кол-во свободных мест на дату %s, равно %d", localDate,
                garageSpotManager.getFreeGarageSpotsQuantityAtDate(masterManager, localDate)));

        Duration requiredDuration = Duration.ofHours(5);
        System.out.println("Ближайшее свободное окно длительностью " + requiredDuration.toHours() + " часов:");
        System.out.println(masterManager.getClosestFreePeriodWithDuration(requiredDuration));
    }

    private static void initManagers() throws InterruptedException {
        initMasters();
        initGarageSpots();
        initOrders();
    }

    public static void initMasters() {
        masterManager.create("Пётр", "Петрович");
        masterManager.create("Афанасий", "Афанасич");
        masterManager.create("Михал", "Михалыч");
    }

    public static void initGarageSpots() {
        for (int i = 1; i <= 7; i++) {
            garageSpotManager.create(i);
        }
    }

    public static void initOrders() throws InterruptedException {
        Optional<GarageSpot> optionalGarageSpot;
        Optional<Master> optionalFreeMaster;
        Period period;
        for (int i = 0; i < 10; i++) {
            period = new Period(LocalDateTime.now().plusHours(i), LocalDateTime.now().plusHours(i + 1));
            optionalGarageSpot = garageSpotManager.getOneGarageSpotFreeInPeriod(period);
            List<Master> freeMastersInPeriod = masterManager.getMastersFreeInPeriod(period);
            optionalFreeMaster = Optional.ofNullable(freeMastersInPeriod.get(random.nextInt(freeMastersInPeriod.size())));
            if (optionalFreeMaster.isPresent() && optionalGarageSpot.isPresent()) {
                orderManager.create((i + 1) * 100, optionalFreeMaster.get(), optionalGarageSpot.get(), period.getStart(),
                        period.getEnd());
                // Добавим временную задержку в 1 секунду, для разных значений createdAt
                Thread.sleep(1000);
            }
        }

        List<Order> createdOrders = orderManager.getOrdersFilteredByStatus(OrderStatus.CREATED);
        // Возьмём в работу созданные заказы
        for (Order order : createdOrders) {
            orderManager.startWorking(order);
        }
    }

    private static void printOrders(List<Order> orders) {
        for (Order order : orders) {
            printOrder(order);
        }
    }

    private static void printOrder(Order order) {
        System.out.println(order);
    }

    private static void printGarageSpots(List<GarageSpot> garageSpots) {
        for (GarageSpot garageSpot : garageSpots) {
            System.out.println(garageSpot);
        }
    }

    private static void printMasters(List<Master> masters) {
        for (Master master : masters) {
            printMaster(master);
        }
    }

    private static void printMaster(Master master) {
        System.out.println(master);
    }
}
