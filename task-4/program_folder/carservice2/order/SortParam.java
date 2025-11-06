package carservice2.order;

import carservice2.common.SortDirection;

public class SortParam {
    private final SortField field;
    private final SortDirection sortDirection;

    public SortParam(SortField field) {
        this.field = field;
        this.sortDirection = SortDirection.ASC;
    }

    public SortParam(SortField field, SortDirection sortDirection) {
        this.field = field;
        this.sortDirection = sortDirection;
    }

    public SortDirection getSortDirection() {
        return sortDirection;
    }

    public SortField getField() {
        return field;
    }
}
