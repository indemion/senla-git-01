package ru.indemion.carservice.models.master;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.dto.MasterDto;
import ru.indemion.carservice.dto.SaveMasterDto;
import ru.indemion.carservice.exceptions.EntityNotFoundException;
import ru.indemion.carservice.models.repositories.MasterRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MasterServiceImpl implements MasterService {
    private final MasterRepository masterRepository;
    private final MasterCsvImporter csvImporter;
    private final MasterCsvExporter csvExporter;
    private final MasterCsvConverter masterCsvConverter;

    public MasterServiceImpl(MasterRepository masterRepository,
                             MasterCsvImporter csvImporter, MasterCsvExporter csvExporter,
                             MasterCsvConverter masterCsvConverter) {
        this.masterRepository = masterRepository;
        this.csvImporter = csvImporter;
        this.csvExporter = csvExporter;
        this.masterCsvConverter = masterCsvConverter;
    }

    @Override
    public Master create(String firstname, String lastname) {
        Master master = new Master(firstname, lastname);
        return masterRepository.save(master);
    }

    @Override
    public void delete(int id) {
        Optional<Master> optionalMaster = masterRepository.findById(id);
        optionalMaster.ifPresent(masterRepository::delete);
    }

    @Override
    public Optional<Master> findById(int id) {
        return masterRepository.findById(id);
    }

    @Override
    public Optional<Master> getMasterByOrderId(int id) {
        return masterRepository.findByOrderId(id);
    }

    @Override
    public List<Master> getMasters() {
        return masterRepository.findAll();
    }

    @Override
    public List<Master> getMastersSorted(SortParams sortParams) {
        return masterRepository.findSorted(sortParams);
    }

    @Override
    public List<Master> getMastersFreeInPeriod(Period period) {
        return masterRepository.findMastersFreeInPeriod(period);
    }

    @Override
    public String exportToPath(String path) {
        return csvExporter.exportToPath(path, getMasters());
    }

    @Override
    public void importFromPath(String path) {
        List<Master> masters = csvImporter.importFromPath(path);
        masterRepository.save(masters);
    }

    @Override
    public void freeMaster(int masterId) {
        Optional<Master> optionalMaster = masterRepository.findById(masterId);
        optionalMaster.ifPresent(master -> {
            master.setOrderAtWork(null);
            masterRepository.save(master);
        });
    }

    @Override
    public void save(Master master) {
        masterRepository.save(master);
    }

    @Override
    public MasterDto find(int id) {
        return masterRepository.findById(id).map(this::convertEntityToDto)
                .orElseThrow(() -> new EntityNotFoundException("Мастер не найден с id: " + id));
    }

    @Override
    public List<MasterDto> findAll(SortParams sortParams) {
        return masterRepository.findSorted(sortParams).stream().map(this::convertEntityToDto).toList();
    }

    @Override
    public MasterDto create(SaveMasterDto masterDto) {
        Master master = new Master(masterDto.getFirstname(), masterDto.getLastname());
        return convertEntityToDto(masterRepository.save(master));
    }

    @Override
    public MasterDto update(int id, SaveMasterDto masterDto) {
        Optional<Master> optionalMaster = masterRepository.findById(id);
        if (optionalMaster.isEmpty()) {
            throw new EntityNotFoundException("Мастер не найден с id: " + id);
        }
        Master master = optionalMaster.get();
        master.setFirstname(masterDto.getFirstname());
        master.setLastname(masterDto.getLastname());
        return convertEntityToDto(masterRepository.save(master));
    }

    @Override
    public String getCsvData() {
        return masterCsvConverter.convert(masterRepository.findAll());
    }

    @Override
    public void importCsv(MultipartFile file) {
        List<Master> masters = csvImporter.importFromMultipartFile(file);
        masterRepository.save(masters);
    }

    private MasterDto convertEntityToDto(Master master) {
        return new MasterDto(master.getId(), master.getFirstname(), master.getLastname(), master.getStatus().toString(),
                master.getOrderAtWorkId());
    }
}
