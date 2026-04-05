package ru.indemion.carservice.dto;

public class GarageSpotDto {
    private int id;
    private int number;
    private String status;
    private Integer orderAtWorkId;

    public GarageSpotDto() {
    }

    public GarageSpotDto(int id, int number, String status, Integer orderAtWorkId) {
        this.id = id;
        this.number = number;
        this.status = status;
        this.orderAtWorkId = orderAtWorkId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getOrderAtWorkId() {
        return orderAtWorkId;
    }

    public void setOrderAtWorkId(Integer orderAtWorkId) {
        this.orderAtWorkId = orderAtWorkId;
    }
}
