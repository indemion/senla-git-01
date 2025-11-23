package carservice5.models.master;

import carservice5.common.SortDirection;

public class SortParam {
    private final SortCriteria sortCriteria;
    private final SortDirection sortDirection;

    public SortParam(SortCriteria sortCriteria) {
        this.sortCriteria = sortCriteria;
        this.sortDirection = SortDirection.ASC;
    }

    public SortParam(SortCriteria sortCriteria, SortDirection sortDirection) {
        this.sortCriteria = sortCriteria;
        this.sortDirection = sortDirection;
    }

    public SortCriteria getSortCriteria() {
        return sortCriteria;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }
}
