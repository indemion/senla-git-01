package carservice6.common;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Period implements Serializable {
    private LocalDateTime start;
    private LocalDateTime end;

    public Period() {
    }

    public Period(LocalDateTime start) {
        this.start = start;
    }

    public Period(LocalDateTime start, LocalDateTime end) {
        if (Objects.isNull(start) || Objects.isNull(end)) {
            throw new RuntimeException("Начало и конец периода не могут быть null");
        }
        if (start.isAfter(end)) throw new IllegalArgumentException(start + " after " + end);
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

    public void setEnd(LocalDateTime end) throws IllegalArgumentException {
        if (start.isAfter(end)) throw new IllegalArgumentException(start + " after " + end);
        this.end = end;
    }

    public void shift(Duration duration) {
        start = start.plus(duration);
        end = end.plus(duration);
    }

    public boolean isOverlap(Period period) {
        return !(end.isBefore(period.getStart()) || end.isEqual(period.getStart()) || start.isAfter(period.getEnd())
                || start.isEqual(period.getEnd()));
    }

    @Override
    public String toString() {
        String startString = start == null ? null : start.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        String endString = end == null ? null : end.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
        return "Period{start=" + startString + ", end=" + endString + "}";
    }

    public static Period newInstance(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) {
            return new Period();
        } else if (end == null) {
            return new Period(start);
        } else {
            return new Period(start, end);
        }
    }
}
