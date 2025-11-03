package carservice2.garage;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Query extends carservice2.common.Query<GarageSpot> {
    Query(List<GarageSpot> garageSpots) {
        super(garageSpots);
    }

    public Query orderByEmpty(boolean ascending) {
        return (Query) addComparator(Comparator.comparing(GarageSpot::isEmpty), ascending);
    }

    public Query filterByEmpty(boolean empty) {
        return (Query) addPredicate((gs) -> gs.isEmpty() == empty);
    }

    public Query filterByNumberNotIn(Set<Integer> spotNumbers) {
        return (Query) addPredicate(gs -> !spotNumbers.contains(gs.getNumber()));
    }
}
