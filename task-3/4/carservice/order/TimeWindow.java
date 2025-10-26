package carservice.order;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeWindow {
    private LocalDateTime start;
    private LocalDateTime end;

    TimeWindow() {}

    TimeWindow(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public void shiftTimeWindow(Duration duration) {
        start = start.plus(duration);
        end = end.plus(duration);
    }

    @Override
    public String toString() {
        return "TimeWindow: начало = " + start.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + ", конец = "
                + end.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
