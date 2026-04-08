package ru.indemion.carservice.dto;

public class CreateGarageSpotDto {
    private int number;

    public CreateGarageSpotDto() {
    }

    public CreateGarageSpotDto(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
