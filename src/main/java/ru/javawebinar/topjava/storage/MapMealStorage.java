package ru.javawebinar.topjava.storage;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.slf4j.LoggerFactory.getLogger;

public class MapMealStorage implements MealStorage {
    private static final Logger log = getLogger(MapMealStorage.class);
    private Map<Integer, Meal> storage = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger();

    @Override
    public Meal get(Integer id) {
        Objects.requireNonNull(id, "id must not be null");
        log.debug("got meal with id=" + id);
        return storage.get(id);
    }

    @Override
    public void save(Meal meal) {
        Objects.requireNonNull(meal, "meal must not be null");
        meal.setId(counter.incrementAndGet());
        storage.put(meal.getId(), meal);
        log.debug("saved meal with id=" + meal.getId());
    }

    @Override
    public void update(Meal meal) {
        Objects.requireNonNull(meal, "meal must not be null");
        log.debug("updated meal with id=" + meal.getId());
        storage.put(meal.getId(), meal);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(Integer id) {
        Objects.requireNonNull(id, "id must not be null");
        log.debug("deleted meal with id=" + id);
        storage.remove(id);
    }

}
