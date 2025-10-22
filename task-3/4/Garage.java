
import java.util.HashMap;
import java.util.Map;

public class Garage {
    private final Map<Integer, GarageSpot> spots = new HashMap<>();

    public void addSpot(GarageSpot spot) {
        spots.putIfAbsent(spot.getNumber(), spot);
    }

    public void removeSpot(GarageSpot spot) {
        spots.remove(spot.getNumber());
    }
}
