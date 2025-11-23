package carservice5.models.garage;

import carservice5.App;
import carservice5.common.OperationProhibitedException;
import carservice5.common.OperationProhibitedMessages;
import carservice5.common.Period;
import carservice5.models.master.MasterService;
import carservice5.models.order.Order;
import carservice5.models.order.OrderService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class GarageSpotService {
    private static GarageSpotService instance;
    private static int lastId = 0;
    private final GarageSpotRepository garageSpotRepository = GarageSpotRepository.instance();
    private final OrderService orderService = OrderService.instance();
    private MasterService masterService;

    private GarageSpotService() {}

    public static GarageSpotService instance() {
        if (instance == null) {
            instance = new GarageSpotService();
        }

        return instance;
    }

    public void setMasterService(MasterService masterService) {
        this.masterService = masterService;
    }

    private static int getNextId() {
        return ++lastId;
    }

    public GarageSpot create(int number) {
        Optional<GarageSpot> optionalGarageSpot = findByNumber(number);
        if (optionalGarageSpot.isPresent()) {
            return optionalGarageSpot.get();
        }
        GarageSpot spot = new GarageSpot(getNextId(), number);
        garageSpotRepository.save(spot);

        return spot;
    }

    public void delete(int id) {
        if (!App.instance().getBooleanProperty("garageSpot.removing")) {
            throw new OperationProhibitedException(OperationProhibitedMessages.GARAGE_SPOT_REMOVING);
        }

        garageSpotRepository.delete(id);
    }

    public List<GarageSpot> getFreeGarageSpotsOnDate(LocalDate localDate) {
        return getFreeGarageSpotsInPeriod(new Period(localDate.atStartOfDay(), localDate.atTime(LocalTime.MAX)));
    }

    public Optional<GarageSpot> getOneGarageSpotFreeInPeriod(Period period) {
        return getFreeGarageSpotsInPeriod(period).stream().findFirst();
    }

    public List<GarageSpot> getFreeGarageSpotsInPeriod(Period period) {
        return query().addPredicate(spot -> orderService.getOrdersFilteredByGarageSpotInPeriod(spot, period).isEmpty())
                .get();
    }

    public Optional<GarageSpot> findById(int id) {
        return query().addPredicate(gs -> gs.getId() == id).first();
    }

    public Optional<GarageSpot> findByNumber(int number) {
        return query().addPredicate(gs -> gs.getNumber() == number).first();
    }

    public List<GarageSpot> getFreeGarageSpots() {
        return query().filterByStatus(GarageSpotStatus.FREE).get();
    }

    public int getFreeGarageSpotsCountAtDate(LocalDate localDate) {
        Period period = new Period(localDate.atStartOfDay(), localDate.atTime(LocalTime.MAX));
        List<Order> orders = orderService.getOrders().stream().filter(order -> order.getEstimatedWorkPeriod()
                .isOverlap(period)).toList();
        Set<Integer> busyMasterIds = new HashSet<>();
        for (Order order : orders) {
            busyMasterIds.add(order.getMaster().getId());
        }
        int freeSpotsQuantity = getFreeGarageSpotsInPeriod(period).size();
        int freeMastersQuantity = masterService.getMastersWhereIdNotIn(busyMasterIds).size();

        return Math.min(freeMastersQuantity, freeSpotsQuantity);
    }

    public List<GarageSpot> getGarageSpots() {
        return query().get();
    }

    public List<GarageSpot> getGarageSpotsByStatus(GarageSpotStatus status) {
        return query().filterByStatus(status).get();
    }

    public Query query() {
        return new Query(garageSpotRepository.findAll());
    }

    public String exportToPath(String path) {
        CsvExporter csvExporter = new CsvExporter();
        return csvExporter.exportToPath(path, query().get());
    }

    public void importFromPath(String path) {
        CsvImporter csvImporter = new CsvImporter();
        List<GarageSpot> garageSpots = csvImporter.importFromPath(path);
        garageSpotRepository.save(garageSpots);
    }
}
