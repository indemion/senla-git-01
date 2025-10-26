package carservice.order;

import carservice.IHasId;

import java.time.Duration;
import java.time.LocalDateTime;

public class Order implements IHasId {
    private static int lastId = 0;
    private final int id;
    private OrderStatus status;
    private final TimeWindow estimatedWorkTimeWindow;
    private final TimeWindow actualWorkTimeWindow;
    private final LocalDateTime createdAt;
    private LocalDateTime closedAt;
    private LocalDateTime canceledAt;

    Order(TimeWindow estimatedWorkTimeWindow) {
        id = getNextId();
        status = OrderStatus.CREATED;
        this.estimatedWorkTimeWindow = estimatedWorkTimeWindow;
        actualWorkTimeWindow = new TimeWindow();
        createdAt = LocalDateTime.now();
    }

    private int getNextId() {
        return ++lastId;
    }

    public int getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public TimeWindow getEstimatedWorkTimeWindow() {
        return estimatedWorkTimeWindow;
    }

    private void setActualEndTime() {
        actualWorkTimeWindow.setEnd(LocalDateTime.now());
    }

    void shiftEstimatedWorkTimeWindow(Duration duration) {
        estimatedWorkTimeWindow.shiftTimeWindow(duration);
    }

    void startWorking() {
        if (status == OrderStatus.WORK_IN_PROGRESS) return;
        status = OrderStatus.WORK_IN_PROGRESS;
        actualWorkTimeWindow.setStart(LocalDateTime.now());
    }

    void close() {
        if (status == OrderStatus.CLOSED) return;
        
        status = OrderStatus.CLOSED;
        closedAt = LocalDateTime.now();
        if (status == OrderStatus.WORK_IN_PROGRESS) {
            setActualEndTime();
        }
    }

    void cancel() {
        if (status == OrderStatus.CANCELED) return;

        status = OrderStatus.CANCELED;
        canceledAt = LocalDateTime.now();
        if (status == OrderStatus.WORK_IN_PROGRESS) {
            setActualEndTime();
        }
    }
}
