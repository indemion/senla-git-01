package ru.indemion.carservice.dao;

import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.master.FilterParams;
import ru.indemion.carservice.models.master.SortParams;

import java.util.List;

public interface MasterDAO extends DAO<MasterDTO> {
    List<MasterDTO> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams);

    List<MasterDTO> findMastersFreeInPeriod(Period period);

    MasterDTO findByOrderId(int id);
}
