package carservice6.ui.views;

import carservice6.models.garage.GarageSpot;

import java.util.List;

public class GarageSpotView {
    public void index(List<GarageSpot> garageSpots) {
        for (GarageSpot garageSpot : garageSpots) {
            System.out.println(garageSpot);
        }
    }

    public void show(GarageSpot garageSpot) {
        System.out.println(garageSpot);
    }
}
