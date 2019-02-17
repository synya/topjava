package ru.javawebinar.topjava.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.ComparisonUtil;
import ru.javawebinar.topjava.util.MealsUtil;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepositoryImpl implements MealRepository {
    private static final Comparator<Meal> DATE_TIME_COMPARATOR =
            Comparator.comparing(Meal::getDateTime)
                    .reversed();
    private Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private AtomicInteger counter = new AtomicInteger(0);

    {
        MealsUtil.MEALS_USER_ID1.forEach(meal -> save(meal, 1));
        MealsUtil.MEALS_USER_ID2.forEach(meal -> save(meal, 2));
    }

    @Override
    public Meal save(Meal meal, int userId) {
        if (meal.isNew()) {
            meal.setId(counter.incrementAndGet());
            repository.computeIfAbsent(userId, ConcurrentHashMap::new).put(meal.getId(), meal);
            return meal;
        }
        return isUserMapExist(userId) ? repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal) : null;
    }

    @Override
    public boolean delete(int id, int userId) {
        return isUserMapExist(userId) && repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        return isUserMapExist(userId) ? repository.get(userId).get(id) : null;
    }

    @Override
    public List<Meal> getAll(int userId) {
        return getUserMeals(userId, m -> true);
    }

    @Override
    public List<Meal> getAllFiltered(int userId, LocalDate startDate, LocalDate endDate) {
        return getUserMeals(userId, m -> ComparisonUtil.isBetween(m.getDate(), startDate, endDate));
    }

    private List<Meal> getUserMeals(int userId, Predicate<Meal> filter) {
        return isUserMapExist(userId) ? repository.get(userId).values().stream()
                .filter(filter)
                .sorted(DATE_TIME_COMPARATOR)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    private boolean isUserMapExist(int userId) {
        return repository.get(userId) != null;
    }

}

