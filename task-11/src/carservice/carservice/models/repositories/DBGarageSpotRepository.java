package carservice.models.repositories;

import carservice.common.Period;
import carservice.dao.GarageSpotDAO;
import carservice.dao.GarageSpotDTO;
import carservice.models.garage.GarageSpot;
import carservice.models.garage.GarageSpotStatus;
import di.Inject;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DBGarageSpotRepository implements GarageSpotRepository {
    private final GarageSpotDAO garageSpotDAO;
    private final GarageSpotMapper mapper;

    @Inject
    public DBGarageSpotRepository(GarageSpotDAO garageSpotDAO, GarageSpotMapper mapper) {
        this.garageSpotDAO = garageSpotDAO;
        this.mapper = mapper;
    }

    @Override
    public GarageSpot save(GarageSpot model) {
        GarageSpotDTO garageSpotDTO = garageSpotDAO.save(mapper.modelToEntity(model));
        model.setId(garageSpotDTO.id());
        return model;
    }

    @Override
    public void save(List<GarageSpot> models) {
        models.forEach(garageSpot -> garageSpotDAO.save(mapper.modelToEntity(garageSpot)));
    }

    @Override
    public void delete(GarageSpot model) {
        garageSpotDAO.delete(model.getId());
    }

    @Override
    public Optional<GarageSpot> findById(int id) {
        GarageSpotDTO garageSpotDTO = garageSpotDAO.findById(id);
        if (garageSpotDTO != null) {
            return Optional.of(mapper.entityToModel(garageSpotDTO));
        }

        return Optional.empty();
    }

    @Override
    public List<GarageSpot> findAll() {
        return garageSpotDAO.findAll().stream().map(mapper::entityToModel).collect(Collectors.toList());
    }

    @Override
    public Optional<GarageSpot> findByNumber(int number) {
        GarageSpotDTO garageSpotDTO = garageSpotDAO.findByNumber(number);
        if (garageSpotDTO != null) {
            return Optional.of(mapper.entityToModel(garageSpotDTO));
        }

        return Optional.empty();
    }

    @Override
    public List<GarageSpot> findFreeGarageSpotsInPeriod(Period period) {
        return garageSpotDAO.findFreeGarageSpotsInPeriod(period).stream().map(mapper::entityToModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<GarageSpot> findFilteredByStatus(GarageSpotStatus status) {
        return garageSpotDAO.findFilteredByStatus(status).stream().map(mapper::entityToModel)
                .collect(Collectors.toList());
    }
}
