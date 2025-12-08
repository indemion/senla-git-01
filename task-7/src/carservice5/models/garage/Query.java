package carservice5.models.garage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Query extends carservice5.common.Query<GarageSpot> {
    Query(List<GarageSpot> garageSpots) {
        super(garageSpots);
    }

    public Query orderByStatus(boolean ascending) {
        return (Query) addComparator(Comparator.comparing(GarageSpot::getStatus), ascending);
    }

    public Query filterByStatus(GarageSpotStatus status) {
        return (Query) addPredicate(gs -> gs.getStatus() == status);
    }

    public Query filterByNumberNotIn(Set<Integer> spotNumbers) {
        return (Query) addPredicate(gs -> !spotNumbers.contains(gs.getNumber()));
    }
}
