package carservice.models.order;

import carservice.common.SortDirection;

public class SortParams {
    private final SortCriteria sortCriteria;
    private final SortDirection sortDirection;

    public SortParams(SortCriteria field) {
        this.sortCriteria = field;
        this.sortDirection = SortDirection.ASC;
    }

    public SortParams(SortCriteria sortCriteria, SortDirection sortDirection) {
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
