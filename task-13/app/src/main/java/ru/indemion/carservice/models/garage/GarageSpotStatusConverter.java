package ru.indemion.carservice.models.garage;

import jakarta.persistence.AttributeConverter;

public class GarageSpotStatusConverter implements AttributeConverter<GarageSpotStatus, String> {
    @Override
    public String convertToDatabaseColumn(GarageSpotStatus garageSpotStatus) {
        return garageSpotStatus.toString();
    }

    @Override
    public GarageSpotStatus convertToEntityAttribute(String s) {
        return GarageSpotStatus.parse(s);
    }
}
