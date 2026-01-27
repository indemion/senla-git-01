package ru.indemion.carservice.models.master;

import ru.indemion.carservice.common.SortDirection;

public class SortParams {
    private final SortCriteria sortCriteria;
    private final SortDirection sortDirection;

    public SortParams(SortCriteria sortCriteria) {
        this.sortCriteria = sortCriteria;
        this.sortDirection = SortDirection.ASC;
    }

    public SortParams(SortCriteria sortCriteria, SortDirection sortDirection) {
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
