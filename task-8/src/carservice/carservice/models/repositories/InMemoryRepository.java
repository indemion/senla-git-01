package carservice.models.repositories;

import carservice.models.IHasId;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class InMemoryRepository<T extends IHasId> implements IRepository<T>, Serializable {
    protected Map<Integer, T> inMemoryDB = new ConcurrentHashMap<>();

    @Override
    public void save(T entity) {
        inMemoryDB.put(entity.getId(), entity);
    }

    @Override
    public void save(List<T> entities) {
        inMemoryDB.putAll(entities.stream().collect(Collectors.toMap(T::getId, Function.identity())));
    }

    @Override
    public void delete(int id) {
        inMemoryDB.remove(id);
    }

    @Override
    public Optional<T> findById(int id) {
        return Optional.ofNullable(inMemoryDB.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(inMemoryDB.values());
    }
}
