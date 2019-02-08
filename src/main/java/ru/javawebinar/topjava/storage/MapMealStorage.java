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
    public Meal get(Integer uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");
        if (!storage.containsKey(uuid)) {
            throw new RuntimeException("cannot find meal with uuid=" + uuid);
        }
        log.debug("got meal with uuid=" + uuid);
        return storage.get(uuid);
    }

    @Override
    public void save(Meal meal) {
        Objects.requireNonNull(meal, "meal must not be null");
        meal.setUuid(counter.incrementAndGet());
        storage.put(meal.getUuid(), meal);
        log.debug("saved meal with uuid=" + meal.getUuid());
    }

    @Override
    public void update(Meal meal) {
        Objects.requireNonNull(meal, "meal must not be null");
        if (!storage.containsKey(meal.getUuid())) {
            throw new RuntimeException("cannot find meal with uuid=" + meal.getUuid());
        }
        log.debug("updated meal with uuid=" + meal.getUuid());
        storage.put(meal.getUuid(), meal);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(Integer uuid) {
        Objects.requireNonNull(uuid, "uuid must not be null");
        if (!storage.containsKey(uuid)) {
            throw new RuntimeException("cannot find meal with uuid=" + uuid);
        }
        log.debug("deleted meal with uuid=" + uuid);
        storage.remove(uuid);
    }

    @Override
    public void clear() {
        log.debug("deleted all meals");
        storage.clear();
        counter.set(0);
    }

}
