package ru.indemion.carservice.models.master;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import ru.indemion.carservice.models.IHasId;
import ru.indemion.carservice.models.order.Order;

import java.util.List;

@Entity
@Table(name = "masters")
public class Master implements IHasId {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstname;
    private String lastname;
    @Convert(converter = MasterStatusConverter.class)
    private MasterStatus status;
    @Column(name = "order_at_work_id", insertable = false, updatable = false)
    private Integer orderAtWorkId;
    @OneToOne(fetch = FetchType.LAZY, targetEntity = Order.class)
    @JoinColumn(name = "order_at_work_id")
    private Order orderAtWork;
    @OneToMany(mappedBy = "master")
    private List<Order> orders;

    public Master() {
    }

    public Master(String firstname, String lastname) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = MasterStatus.FREE;
    }

    public Master(int id, String firstname, String lastname, MasterStatus status, Order orderAtWork) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = status;
        this.orderAtWork = orderAtWork;
    }

    public Master(int id, String firstname, String lastname, MasterStatus status, Integer orderAtWorkId) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
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
        this.status = orderAtWork == null ? MasterStatus.FREE : MasterStatus.BUSY;
    }

    @Override
    public String toString() {
        return "Master{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", status=" + status +
                ", orderAtWorkId=" + orderAtWorkId +
                '}';
    }
}
