package carservice5.models.master;

import carservice5.common.SortDirection;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class Query extends carservice5.common.Query<Master> {
    public Query(List<Master> entities) {
        super(entities);
    }

    public Query orderByFullname(boolean ascending) {
        return (Query) addComparator(Comparator.comparing(Master::getFullname), ascending);
    }

    public Query orderByStatus(boolean ascending) {
        return (Query) addComparator(Comparator.comparing(Master::getStatus), ascending);
    }

    public Query filterByBusy(boolean busy) {
        return (Query) addPredicate(master -> master.isBusy() == busy);
    }

    public Query filterByIdNotIn(Set<Integer> busyMasterIds) {
        return (Query) addPredicate(master -> !busyMasterIds.contains(master.getId()));
    }

    public Query orderBy(SortParam sortParam) {
        boolean ascending = sortParam.getSortDirection() == SortDirection.ASC;
        switch (sortParam.getSortCriteria()) {
            case FULLNAME -> orderByFullname(ascending);
            case STATUS -> orderByStatus(ascending);
        }
        return this;
    }

    public Query filterByStatus(MasterStatus status) {
        return (Query) addPredicate(master -> master.getStatus() == status);
    }
}
