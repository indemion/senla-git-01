package ru.indemion.carservice.models.master;

import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.repositories.MasterRepository;
import ru.indemion.carservice.models.services.AbstractTransactionalService;
import ru.indemion.carservice.util.HibernateUtil;
import ru.indemion.di.Container;
import ru.indemion.di.Inject;

import java.util.List;
import java.util.Optional;

public class MasterService extends AbstractTransactionalService {
    private final MasterRepository masterRepository;

    @Inject
    public MasterService(MasterRepository masterRepository) {
        super(HibernateUtil.getCurrentSession());
        this.masterRepository = masterRepository;
    }

    public Master create(String firstname, String lastname) {
        Master master = new Master(firstname, lastname);
        return inTransaction(() -> masterRepository.save(master));
    }

    public void delete(int id) {
        Optional<Master> optionalMaster = masterRepository.findById(id);
        optionalMaster.ifPresent(master -> inTransaction(() -> masterRepository.delete(master)));
    }

    public Optional<Master> findById(int id) {
        return masterRepository.findById(id);
    }

    public Optional<Master> getMasterByOrderId(int id) {
        return masterRepository.findByOrderId(id);
    }

    public List<Master> getMasters() {
        return masterRepository.findAll();
    }

    public List<Master> getMastersSorted(SortParams sortParams) {
        return masterRepository.findSorted(sortParams);
    }

    public List<Master> getMastersFreeInPeriod(Period period) {
        return masterRepository.findMastersFreeInPeriod(period);
    }

    public String exportToPath(String path) {
        CsvExporter csvExporter = new CsvExporter();
        return csvExporter.exportToPath(path, getMasters());
    }

    public void importFromPath(String path) {
        CsvImporter csvImporter = Container.INSTANCE.resolve(CsvImporter.class);
        List<Master> masters = csvImporter.importFromPath(path);
        inTransaction(() -> masterRepository.save(masters));
    }

    public void freeMaster(int masterId) {
        Optional<Master> optionalMaster = masterRepository.findById(masterId);
        optionalMaster.ifPresent(master -> {
            master.setOrderAtWork(null);
            masterRepository.save(master);
        });
    }

    public void save(Master master) {
        masterRepository.save(master);
    }
}
