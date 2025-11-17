package carservice4.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Query<T> implements IQuery<T> {
    private final List<Comparator<T>> comparators = new ArrayList<>();
    private final List<Predicate<T>> predicates = new ArrayList<>();
    private List<T> currentEntities;

    public Query(List<T> entities) {
        currentEntities = new ArrayList<>(entities);
    }

    public Query<T> addPredicate(Predicate<T> predicate) {
        predicates.add(predicate);
        return this;
    }

    public Query<T> addComparator(Comparator<T> comparator, boolean ascending) {
        if (!ascending) {
            comparator = comparator.reversed();
        }
        comparators.add(comparator);
        return this;
    }

    @Override
    public List<T> get() {
        applyPredicates();
        applyComparators();

        return currentEntities;
    }

    @Override
    public Optional<T> first() {
        applyPredicates();
        applyComparators();

        return currentEntities.stream().findFirst();
    }

    private void applyPredicates() {
        if (!predicates.isEmpty()) {
            Predicate<T> predicate = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                predicate = predicate.and(predicates.get(i));
            }
            currentEntities = currentEntities.stream().filter(predicate).collect(Collectors.toList());
        }
    }

    private void applyComparators() {
        if (!comparators.isEmpty()) {
            Comparator<T> comparator = comparators.get(0);
            for (int i = 1; i < comparators.size(); i++) {
                comparator = comparator.thenComparing(comparators.get(i));
            }
            currentEntities.sort(comparator);
        }
    }
}
