package ru.indemion.carservice.models.repositories;

import ru.indemion.carservice.dao.GarageSpotDTO;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.garage.GarageSpotStatus;

public class GarageSpotMapper implements Mapper<GarageSpot, GarageSpotDTO> {
    @Override
    public GarageSpot entityToModel(GarageSpotDTO entity) {
        return new GarageSpot(entity.id(), entity.number(), GarageSpotStatus.parse(entity.status()),
                entity.orderAtWorkId());
    }

    @Override
    public GarageSpotDTO modelToEntity(GarageSpot model) {
        return new GarageSpotDTO(model.getId(), model.getNumber(), model.getStatus().toString(),
                model.getOrderAtWorkId());
    }
}
