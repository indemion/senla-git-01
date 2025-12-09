package carservice.models.master;

import carservice.models.Entity;
import carservice.models.order.Order;

public class Master extends Entity {
    private final String firstname;
    private final String lastname;
    private MasterStatus status;
    private Integer orderAtWorkId;
    private transient Order orderAtWork;

    public Master(int id, String firstname, String lastname) {
        super(id);
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = MasterStatus.FREE;
    }

    public Master(int id, String firstname, String lastname, MasterStatus status, Order orderAtWork) {
        super(id);
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = status;
        this.orderAtWorkId = orderAtWork.getId();
        this.orderAtWork = orderAtWork;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFullname() {
        return firstname + " " + lastname;
    }

    public MasterStatus getStatus() {
        return status;
    }

    public Integer getOrderAtWorkId() {
        return orderAtWorkId;
    }

    public Order getOrderAtWork() {
        return orderAtWork;
    }

    public void setOrderAtWork(Order orderAtWork) {
        this.orderAtWork = orderAtWork;
        this.orderAtWorkId = orderAtWork == null ? null : orderAtWork.getId();
        this.status = orderAtWork == null ? MasterStatus.FREE : MasterStatus.BUSY;
    }

    public boolean isBusy() {
        return status == MasterStatus.BUSY;
    }

    @Override
    public String toString() {
        return String.format("""
                Master:
                 - id: %d
                 - firstname: %s
                 - lastname: %s
                 - status: %s""", id, firstname, lastname, status);
    }

}
