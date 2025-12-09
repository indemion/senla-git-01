package carservice.ui.views;

import carservice.models.garage.GarageSpot;

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
