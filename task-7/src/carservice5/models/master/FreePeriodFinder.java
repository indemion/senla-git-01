package carservice5.models.master;

import carservice5.common.Period;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FreePeriodFinder {
    public static Period findClosestFreePeriod(List<Period> occupiedPeriods, Duration requiredDuration) {
        List<Period> periods = new ArrayList<>(occupiedPeriods);
        periods.sort(Comparator.comparing(Period::getStart));
        LocalDateTime currentPointer = LocalDateTime.now();
        for (Period period : periods) {
            // Проверяем что текущий указатель времени находится раньше ближайшего периода
            if (currentPointer.isBefore(period.getStart())) {
                // Проверяем что между текущим указателем времени и началом следующего периода есть запрашиваемая
                // длительность
                if (Duration.between(currentPointer, period.getStart()).compareTo(requiredDuration) >= 0) {
                    return new Period(currentPointer, currentPointer.plus(requiredDuration));
                } else {
                    // Если между текущим указателем и началом периода недостаточно времени, то меняем текущий указатель
                    // на конец периода
                    currentPointer = period.getEnd();
                }
            }
            if (currentPointer.isBefore(period.getEnd())) {
                // Если текущий указатель оказался внутри периода, то меняем его на конец периода
                currentPointer = period.getEnd();
            }
        }

        return new Period(currentPointer, currentPointer.plus(requiredDuration));
    }
}
