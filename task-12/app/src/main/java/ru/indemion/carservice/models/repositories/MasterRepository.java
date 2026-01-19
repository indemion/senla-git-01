package ru.indemion.carservice.models.repositories;

import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.master.FilterParams;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.SortParams;

import java.util.List;
import java.util.Optional;

public interface MasterRepository extends Repository<Master> {
    List<Master> findSorted(SortParams sortParams);

    List<Master> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams);

    List<Master> findMastersFreeInPeriod(Period period);

    Optional<Master> findByOrderId(int id);
}
