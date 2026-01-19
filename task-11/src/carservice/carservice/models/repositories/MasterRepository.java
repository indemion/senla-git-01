package carservice.models.repositories;

import carservice.common.Period;
import carservice.models.master.FilterParams;
import carservice.models.master.Master;
import carservice.models.master.SortParams;

import java.util.List;
import java.util.Optional;

public interface MasterRepository extends Repository<Master> {
    List<Master> findSorted(SortParams sortParams);
    List<Master> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams);
    List<Master> findMastersFreeInPeriod(Period period);
    Optional<Master> findByOrderId(int id);
}
