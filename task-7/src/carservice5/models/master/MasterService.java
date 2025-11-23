package carservice5.models.master;

import carservice5.common.EntityNotFoundException;
import carservice5.common.Period;
import carservice5.models.order.Order;
import carservice5.models.order.OrderService;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class MasterService {
    private static MasterService instance;
    private static int lastId = 0;
    private final MasterRepository masterRepository = MasterRepository.instance();
    private OrderService orderService;

    private MasterService() {}

    public static MasterService instance() {
        if (instance == null) {
            instance = new MasterService();
        }
        return instance;
    }

    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    private static int getNextId() {
        return ++lastId;
    }

    public Master create(String firstname, String lastname) {
        Master master = new Master(getNextId(), firstname, lastname);
        masterRepository.save(master);

        return master;
    }

    public void delete(int id) {
        Optional<Master> optionalMaster = masterRepository.findById(id);
        optionalMaster.ifPresent(master -> masterRepository.delete(id));
    }

    public Optional<Master> findById(int id) {
        return masterRepository.findById(id);
    }

    public Master getMasterByOrderId(int id) throws EntityNotFoundException {
        Optional<Order> optionalOrder = orderService.findById(id);
        if (optionalOrder.isEmpty()) {
            throw new EntityNotFoundException("Заказ с id " + id + " не найден.");
        }

        return optionalOrder.get().getMaster();
    }

    public List<Master> getMasters() {
        return query().get();
    }

    public List<Master> getMastersSorted(SortParam sortParam) {
        return query().orderBy(sortParam).get();
    }

    public List<Master> getMastersFilteredByStatus(MasterStatus status) {
        return query().filterByStatus(status).get();
    }

    public List<Master> getMastersWhereIdNotIn(Set<Integer> busyMasterIds) {
        return query().filterByIdNotIn(busyMasterIds).get();
    }

    public List<Master> getMastersFreeInPeriod(Period period) {
        return query().addPredicate(master ->
                orderService.getOrdersByMasterAndEstimatedWorkPeriodOverlapPeriod(master, period).isEmpty()).get();
    }

    public Period getClosestFreePeriodWithDuration(Duration requiredDuration) {
        List<List<Period>> periodsList = new ArrayList<>();
        query().get().forEach(master -> {
            List<Order> orders = orderService.getOrdersByMasterCreatedAndWIP(master);
            periodsList.add(orders.stream().map(Order::getEstimatedWorkPeriod).collect(Collectors.toList()));
        });
        periodsList.forEach(periods -> periods.sort(Comparator.comparing(Period::getStart)));

        List<Period> freePeriods = new ArrayList<>();
        for (List<Period> periods : periodsList) {
            freePeriods.add(FreePeriodFinder.findClosestFreePeriod(periods, requiredDuration));
        }
        freePeriods.sort(Comparator.comparing(Period::getStart));

        return freePeriods.stream().findFirst().get();
    }

    public Query query() {
        return new Query(masterRepository.findAll());
    }

    public String exportToPath(String path) {
        CsvExporter csvExporter = new CsvExporter();
        return csvExporter.exportToPath(path, query().get());
    }

    public void importFromPath(String path) {
        CsvImporter csvImporter = new CsvImporter();
        List<Master> masters = csvImporter.importFromPath(path);
        masterRepository.save(masters);
    }
}
