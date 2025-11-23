package carservice5.models.garage;

import carservice5.models.garage.GarageSpot;
import carservice5.models.repositories.InMemoryRepository;

public class GarageSpotRepository extends InMemoryRepository<GarageSpot> {
    private static GarageSpotRepository instance;

    private GarageSpotRepository() {
    }

    public static GarageSpotRepository instance() {
        if (instance == null) {
            instance = new GarageSpotRepository();
        }

        return instance;
    }
}
