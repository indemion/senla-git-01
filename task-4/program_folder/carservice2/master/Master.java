package carservice2.master;

import carservice2.order.Order;

public class Master {
    private static int lastId = 0;
    private final int id;
    private final String firstname;
    private final String lastname;
    private MasterStatus status;
    private Order orderAtWork;

    public Master(String firstname, String lastname) {
        this.id = getNextId();
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = MasterStatus.FREE;
    }

    private int getNextId() {
        return ++lastId;
    }

    public int getId() {
        return id;
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
