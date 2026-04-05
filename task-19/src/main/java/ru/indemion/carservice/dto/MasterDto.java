package ru.indemion.carservice.dto;

public class MasterDto {
    private int id;
    private String firstname;
    private String lastname;
    private String status;
    private Integer orderAtWorkId;

    public MasterDto() {
    }

    public MasterDto(int id, String firstname, String lastname, String status, Integer orderAtWorkId) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = status;
        this.orderAtWorkId = orderAtWorkId;
    }

    public int getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getStatus() {
        return status;
    }

    public Integer getOrderAtWorkId() {
        return orderAtWorkId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderAtWorkId(Integer orderAtWorkId) {
        this.orderAtWorkId = orderAtWorkId;
    }
}
