package carservice5.models.master;

import carservice5.models.Entity;
import carservice5.models.master.MasterStatus;
import carservice5.models.order.Order;

public class Master extends Entity {
    private final String firstname;
    private final String lastname;
    private MasterStatus status;
    private Order orderAtWork;

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

    public Order getOrderAtWork() {
        return orderAtWork;
    }

    public void setOrderAtWork(Order orderAtWork) {
        this.status = orderAtWork == null ? MasterStatus.FREE : MasterStatus.BUSY;
        this.orderAtWork = orderAtWork;
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
