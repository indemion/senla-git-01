package ru.indemion.carservice.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class CreateOrderDto {
    private int price;
    private int masterId;
    private int garageSpotId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedWorkPeriodStart;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime estimatedWorkPeriodEnd;

    public CreateOrderDto() {
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
}
