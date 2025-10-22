import java.util.Date;

public class Order {
    private int number;
    private OrderStatus status;
    private Date createdAt;
    private Date closedAt;
    private Date canceledAt;

    public Order(int number) {
        this.number = number;
        status = OrderStatus.CREATED;
        createdAt = new Date();
    }

    public void close() {
        if (status == OrderStatus.CLOSED) return;
        
        status = OrderStatus.CLOSED;
        closedAt = new Date();
    }

    public void cancel() {
        if (status == OrderStatus.CANCELED) return;

        status = OrderStatus.CANCELED;
        canceledAt = new Date();
    }

    public enum OrderStatus {
        CREATED,
        CLOSED,
        CANCELED
    }
}
