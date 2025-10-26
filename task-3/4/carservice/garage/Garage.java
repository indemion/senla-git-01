package carservice.garage;

import java.util.HashMap;
import java.util.Map;

public class Garage {
    private final Map<Integer, GarageSpot> spots = new HashMap<>();

    public GarageSpot createSpot(int number) {
        if (spots.containsKey(number)) {
            System.out.printf("Место с номером %d уже существует в гараже, укажите другой номер%n", number);
            return null;
        }
        GarageSpot spot = new GarageSpot(number);
        spots.put(spot.getNumber(), spot);
        System.out.printf("Место с номером %d добавлено в гараж%n", spot.getNumber());

        return spot;
    }

    public void removeSpot(int number) {
        if (!spots.containsKey(number)) {
            return;
        }

        spots.remove(number);
        System.out.printf("Место с номером %d удалено из гаража%n", number);
    }
}
