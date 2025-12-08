package carservice6.common;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface IQuery<T> {
    IQuery<T> addPredicate(Predicate<T> predicate);

    IQuery<T> addComparator(Comparator<T> comparator, boolean ascending);

    List<T> get();

    Optional<T> first();
}