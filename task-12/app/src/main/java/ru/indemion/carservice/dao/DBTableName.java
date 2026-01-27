package ru.indemion.carservice.dao;

public enum DBTableName {
    MASTERS("masters"),
    ORDERS("orders"),
    GARAGE_SPOTS("garage_spots");

    private final String tableName;

    DBTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }
}
