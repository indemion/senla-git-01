package carservice4.models.order;

import carservice4.common.SortDirection;

public class SortParam {
    private final SortCriteria sortCriteria;
    private final SortDirection sortDirection;

    public SortParam(SortCriteria field) {
        this.sortCriteria = field;
        this.sortDirection = SortDirection.ASC;
    }

    public SortParam(SortCriteria sortCriteria, SortDirection sortDirection) {
        this.sortCriteria = sortCriteria;
        this.sortDirection = sortDirection;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public SortCriteria getSortCriteria() {
        return sortCriteria;
    }
}
