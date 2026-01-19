package carservice.dao;

import carservice.common.Period;
import carservice.models.master.FilterParams;
import carservice.models.master.SortParams;

import java.util.List;

public interface MasterDAO extends DAO<MasterDTO> {
    List<MasterDTO> findFilteredAndSorted(FilterParams filterParams, SortParams sortParams);
    List<MasterDTO> findMastersFreeInPeriod(Period period);
    MasterDTO findByOrderId(int id);
}
