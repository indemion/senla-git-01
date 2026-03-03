package ru.indemion.carservice.dto;

import ru.indemion.carservice.common.HealthStatus;

public class HealthResponseDto {
    private HealthStatus status;
    private HealthStatus dbStatus;

    public HealthResponseDto() {
    }

    public HealthResponseDto(HealthStatus status, HealthStatus dbStatus) {
        this.status = status;
        this.dbStatus = dbStatus;
    }

    public HealthStatus getStatus() {
        return status;
    }

    public HealthStatus getDbStatus() {
        return dbStatus;
    }

    public void setStatus(HealthStatus status) {
        this.status = status;
    }

    public void setDbStatus(HealthStatus dbStatus) {
        this.dbStatus = dbStatus;
    }
}
