package ru.indemion.carservice.common;

public class SortParams<T> {
    private final T sortCriteria;
    private final SortDirection sortDirection;

    public SortParams(T sortCriteria) {
        this.sortCriteria = sortCriteria;
        this.sortDirection = SortDirection.ASC;
    }

    public SortParams(T sortCriteria, SortDirection sortDirection) {
        this.sortCriteria = sortCriteria;
        this.sortDirection = sortDirection;
    }

    public T getSortCriteria() {
        return sortCriteria;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }
}
