package carservice.models.repositories;

import carservice.dao.GarageSpotDTO;
import carservice.models.garage.GarageSpot;
import carservice.models.garage.GarageSpotStatus;

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
