package carservice.models.repositories;

import carservice.common.Period;
import carservice.dao.MasterDAO;
import carservice.dao.MasterDTO;
import carservice.models.master.FilterParams;
import carservice.models.master.Master;
import carservice.models.master.SortParams;
import di.Inject;

import java.util.List;
import java.util.Optional;

public class DBMasterRepository implements MasterRepository {
    private final MasterDAO masterDAO;

    @Inject
    public DBMasterRepository(MasterDAO masterDAO) {
        this.masterDAO = masterDAO;
    }

    @Override
    public Master save(Master master) {
        MasterDTO masterDTO = masterDAO.save(master.toEntity());
        master.setId(masterDTO.id());
        return master;
    }

    @Override
    public void save(List<Master> masters) {
        masters.forEach(master -> masterDAO.save(master.toEntity()));
    }

    @Override
    public void delete(Master master) {
        masterDAO.delete(master.getId());
    }

    @Override
    public Optional<Master> findById(int id) {
        MasterDTO masterDTO = masterDAO.findById(id);
        if (masterDTO != null) {
            return Optional.of(masterDTO.toModel());
        }

        return Optional.empty();
    }

    @Override
    public List<Master> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams) {
        return masterDAO.findFilteredAndSorted(filterParams, sortParams).stream().map(MasterDTO::toModel).toList();
    }

    @Override
    public List<Master> findAll() {
        return findFilteredAndSorted(null, null);
    }

    @Override
    public List<Master> findSorted(SortParams sortParams) {
        return findFilteredAndSorted(null, sortParams);
    }

    @Override
    public List<Master> findMastersFreeInPeriod(Period period) {
        return masterDAO.findMastersFreeInPeriod(period).stream().map(MasterDTO::toModel).toList();
    }

    @Override
    public Optional<Master> findByOrderId(int id) {
        MasterDTO masterDTO = masterDAO.findByOrderId(id);
        if (masterDTO != null) {
            return Optional.of(masterDTO.toModel());
        }

        return Optional.empty();
    }
}
