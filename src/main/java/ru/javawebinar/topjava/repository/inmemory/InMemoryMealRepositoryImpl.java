package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.ComparisonUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private static final Comparator<Meal> DATE_TIME_COMPARATOR =
            Comparator.comparing(Meal::getDateTime)
                    .reversed();
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS.forEach(meal -> save(meal, meal.getUserId()));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            meal.setUserId(userId);
            repository.computeIfAbsent(userId, ConcurrentHashMap::new).put(meal.getId(), meal);
            return meal;
        }
        return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> {
            if (oldMeal.getUserId() == userId) {
                meal.setUserId(userId);
                return meal;
            }
            return oldMeal;
        });
    }

    @Override
    public boolean delete(int id, int userId) {
        Meal meal = repository.get(userId).get(id);
        if (meal != null) {
            return meal.getUserId() == userId && repository.get(userId).remove(id) != null;
        }
        return false;
    }

    @Override
    public Meal get(int id, int userId) {
        Meal meal = repository.get(userId).get(id);
        if (meal != null) {
            return meal.getUserId() == userId ? meal : null;
        }
        return null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return repository.get(userId).values().stream()
                .sorted(DATE_TIME_COMPARATOR)
                .collect(Collectors.toList());
    }

    @Override
    public List<Meal> getAll(int userId, LocalDate startDate, LocalDate endDate) {
        return getAll(userId).stream()
                .filter(m -> ComparisonUtil.isBetween(m.getDate(), startDate, endDate))
                .collect(Collectors.toList());
    }
}

