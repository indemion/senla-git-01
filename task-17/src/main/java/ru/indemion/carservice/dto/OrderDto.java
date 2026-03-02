package ru.indemion.carservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class OrderDto {
    private int id;
    private int price;
    private int masterId;
    private int garageSpotId;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedWorkPeriodStart;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedWorkPeriodEnd;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualWorkPeriodStart;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualWorkPeriodEnd;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime closedAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime canceledAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deletedAt;

    public OrderDto() {
    }

    public OrderDto(int id, int price, int masterId, int garageSpotId, String status,
                    LocalDateTime estimatedWorkPeriodStart, LocalDateTime estimatedWorkPeriodEnd,
                    LocalDateTime actualWorkPeriodStart, LocalDateTime actualWorkPeriodEnd,
                    LocalDateTime createdAt, LocalDateTime closedAt, LocalDateTime canceledAt, LocalDateTime deletedAt) {
        this.id = id;
        this.price = price;
        this.masterId = masterId;
        this.garageSpotId = garageSpotId;
        this.status = status;
        this.estimatedWorkPeriodStart = estimatedWorkPeriodStart;
        this.estimatedWorkPeriodEnd = estimatedWorkPeriodEnd;
        this.actualWorkPeriodStart = actualWorkPeriodStart;
        this.actualWorkPeriodEnd = actualWorkPeriodEnd;
        this.createdAt = createdAt;
        this.closedAt = closedAt;
        this.canceledAt = canceledAt;
        this.deletedAt = deletedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMasterId() {
        return masterId;
    }

    public void setMasterId(int masterId) {
        this.masterId = masterId;
    }

    public int getGarageSpotId() {
        return garageSpotId;
    }

    public void setGarageSpotId(int garageSpotId) {
        this.garageSpotId = garageSpotId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEstimatedWorkPeriodStart() {
        return estimatedWorkPeriodStart;
    }

    public void setEstimatedWorkPeriodStart(LocalDateTime estimatedWorkPeriodStart) {
        this.estimatedWorkPeriodStart = estimatedWorkPeriodStart;
    }

    public LocalDateTime getEstimatedWorkPeriodEnd() {
        return estimatedWorkPeriodEnd;
    }

    public void setEstimatedWorkPeriodEnd(LocalDateTime estimatedWorkPeriodEnd) {
        this.estimatedWorkPeriodEnd = estimatedWorkPeriodEnd;
    }

    public LocalDateTime getActualWorkPeriodStart() {
        return actualWorkPeriodStart;
    }

    public void setActualWorkPeriodStart(LocalDateTime actualWorkPeriodStart) {
        this.actualWorkPeriodStart = actualWorkPeriodStart;
    }

    public LocalDateTime getActualWorkPeriodEnd() {
        return actualWorkPeriodEnd;
    }

    public void setActualWorkPeriodEnd(LocalDateTime actualWorkPeriodEnd) {
        this.actualWorkPeriodEnd = actualWorkPeriodEnd;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }

    public LocalDateTime getCanceledAt() {
        return canceledAt;
    }

    public void setCanceledAt(LocalDateTime canceledAt) {
        this.canceledAt = canceledAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
