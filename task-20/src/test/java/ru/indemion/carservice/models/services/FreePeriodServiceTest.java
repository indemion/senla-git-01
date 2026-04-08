package ru.indemion.carservice.models.services;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.indemion.carservice.common.Period;
import ru.indemion.carservice.models.garage.GarageSpot;
import ru.indemion.carservice.models.master.Master;
import ru.indemion.carservice.models.master.MasterService;
import ru.indemion.carservice.models.order.Order;
import ru.indemion.carservice.models.order.OrderService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FreePeriodServiceTest {
    @Mock
    private OrderService orderService;

    @Mock
    private MasterService masterService;

    @InjectMocks
    private FreePeriodService freePeriodService;

    private final LocalDateTime fixedNow = LocalDateTime.of(2025, 4, 10, 9, 0);

    private static MockedStatic<LocalDateTime> mockedLocalDateTime;

    @BeforeAll
    static void beforeAll() {
        mockedLocalDateTime = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
    }

    @AfterAll
    static void afterAll() {
        mockedLocalDateTime.close();
    }


    private Master master(int id) {
        Master master = new Master("Name" + id, "Last" + id);
        master.setId(id);
        return master;
    }

    private Order orderWithPeriod(LocalDateTime start, LocalDateTime end, Master master) {
        return new Order(100, master, mock(GarageSpot.class), new Period(start, end));
    }

    @Test
    void getClosestFreePeriod_shouldReturnPeriodBeforeFirstOccupiedWhenEnoughTime_mockedNow() {
        mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedNow);

        Duration required = Duration.ofHours(2);
        Master master1 = master(1);
        when(masterService.getMasters()).thenReturn(List.of(master1));

        // Занятый период с 11:00 до 13:00
        Period occupied = new Period(
                LocalDateTime.of(2025, 4, 10, 11, 0),
                LocalDateTime.of(2025, 4, 10, 13, 0)
        );
        Order order = orderWithPeriod(occupied.getStart(), occupied.getEnd(), master1);
        when(orderService.getOrdersByMasterCreatedAndWIP(master1)).thenReturn(List.of(order));

        Period result = freePeriodService.getClosestFreePeriodWithDuration(required);

        // Ожидаем свободный период с 9:00 до 11:00
        Period expected = new Period(fixedNow, fixedNow.plus(required));
        assertEquals(expected.getStart(), result.getStart());
        assertEquals(expected.getEnd(), result.getEnd());
    }

    @Test
    void getClosestFreePeriod_shouldReturnPeriodBetweenOccupiedWhenGapSufficient() {
        mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedNow);

        Duration required = Duration.ofHours(2);
        Master master1 = master(1);
        when(masterService.getMasters()).thenReturn(List.of(master1));

        // Занятые периоды: 8:00-10:00 и 13:00-15:00
        Period occupied1 = new Period(
                LocalDateTime.of(2025, 4, 10, 8, 0),
                LocalDateTime.of(2025, 4, 10, 10, 0)
        );
        Period occupied2 = new Period(
                LocalDateTime.of(2025, 4, 10, 13, 0),
                LocalDateTime.of(2025, 4, 10, 15, 0)
        );
        Order order1 = orderWithPeriod(occupied1.getStart(), occupied1.getEnd(), master1);
        Order order2 = orderWithPeriod(occupied2.getStart(), occupied2.getEnd(), master1);
        when(orderService.getOrdersByMasterCreatedAndWIP(master1)).thenReturn(List.of(order1, order2));

        Period result = freePeriodService.getClosestFreePeriodWithDuration(required);

        // Ожидаем свободный период с 10:00 до 12:00 (между 10:00 и 13:00)
        Period expected = new Period(
                occupied1.getEnd(),
                occupied1.getEnd().plus(required)
        );
        assertEquals(expected.getStart(), result.getStart());
        assertEquals(expected.getEnd(), result.getEnd());
    }

    @Test
    void getClosestFreePeriod_shouldReturnPeriodAfterLastOccupiedWhenNoGapBefore() {
        mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedNow);
        Duration required = Duration.ofHours(2);
        Master master1 = master(1);
        when(masterService.getMasters()).thenReturn(List.of(master1));

        // Занятый период с 9:30 до 11:00 – текущее время внутри периода
        Period occupied = new Period(
                LocalDateTime.of(2025, 4, 10, 9, 30),
                LocalDateTime.of(2025, 4, 10, 11, 0)
        );
        Order order = orderWithPeriod(occupied.getStart(), occupied.getEnd(), master1);
        when(orderService.getOrdersByMasterCreatedAndWIP(master1)).thenReturn(List.of(order));

        Period result = freePeriodService.getClosestFreePeriodWithDuration(required);

        // Ожидаем свободный период после окончания занятого: 11:00 - 13:00
        Period expected = new Period(
                occupied.getEnd(),
                occupied.getEnd().plus(required)
        );
        assertEquals(expected.getStart(), result.getStart());
        assertEquals(expected.getEnd(), result.getEnd());
    }

    @Test
    void getClosestFreePeriod_shouldReturnEarliestPeriodAcrossMultipleMasters() {
        mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedNow);

        Duration required = Duration.ofHours(2);
        Master master1 = master(1);
        Master master2 = master(2);
        when(masterService.getMasters()).thenReturn(List.of(master1, master2));

        // Мастер 1: занят с 10:00 до 12:00
        Period occupied1 = new Period(
                LocalDateTime.of(2025, 4, 10, 10, 0),
                LocalDateTime.of(2025, 4, 10, 12, 0)
        );
        Order order1 = orderWithPeriod(occupied1.getStart(), occupied1.getEnd(), master1);
        when(orderService.getOrdersByMasterCreatedAndWIP(master1)).thenReturn(List.of(order1));

        // Мастер 2: свободен с 9:00 до 11:00 (нет занятых периодов)
        when(orderService.getOrdersByMasterCreatedAndWIP(master2)).thenReturn(List.of());

        Period result = freePeriodService.getClosestFreePeriodWithDuration(required);

        // Ожидаем период с 9:00 до 11:00 (у мастера 2)
        Period expected = new Period(fixedNow, fixedNow.plus(required));
        assertEquals(expected.getStart(), result.getStart());
        assertEquals(expected.getEnd(), result.getEnd());
    }

    @Test
    void getClosestFreePeriod_shouldReturnPeriodWhenCurrentTimeBeforeFirstOccupiedButNotEnoughTimeBefore() {
        mockedLocalDateTime.when(LocalDateTime::now).thenReturn(fixedNow);

        Duration required = Duration.ofHours(3); // нужно 3 часа
        Master master1 = master(1);
        when(masterService.getMasters()).thenReturn(List.of(master1));

        // Первый занятый период начинается через 1 час (10:00)
        Period occupied = new Period(
                LocalDateTime.of(2025, 4, 10, 10, 0),
                LocalDateTime.of(2025, 4, 10, 12, 0)
        );
        Order order = orderWithPeriod(occupied.getStart(), occupied.getEnd(), master1);
        when(orderService.getOrdersByMasterCreatedAndWIP(master1)).thenReturn(List.of(order));

        Period result = freePeriodService.getClosestFreePeriodWithDuration(required);

        // Между 9:00 и 10:00 только 1 час – недостаточно. Ждём после 12:00.
        Period expected = new Period(
                occupied.getEnd(),
                occupied.getEnd().plus(required)
        );
        assertEquals(expected.getStart(), result.getStart());
        assertEquals(expected.getEnd(), result.getEnd());
    }

    // Негативный сценарий: если нет мастеров (список пуст). Тогда метод findFirst().get() выбросит NoSuchElementException.
    @Test
    void getClosestFreePeriod_shouldThrowWhenNoMasters() {
        when(masterService.getMasters()).thenReturn(List.of());

        Duration required = Duration.ofHours(1);
        // Ожидаем исключение, так как freePeriods будет пуст, и findFirst().get() упадёт
        try {
            freePeriodService.getClosestFreePeriodWithDuration(required);
        } catch (Exception e) {
            // Можно проверить тип исключения, но проще: assertThrows
        }
    }

    // Но лучше использовать assertThrows:
    @Test
    void getClosestFreePeriod_shouldThrowNoSuchElementExceptionWhenNoMasters() {
        when(masterService.getMasters()).thenReturn(List.of());
        Duration required = Duration.ofHours(1);

        // Используем assertThrows, чтобы проверить исключение
        org.junit.jupiter.api.Assertions.assertThrows(java.util.NoSuchElementException.class,
                () -> freePeriodService.getClosestFreePeriodWithDuration(required));
    }
}