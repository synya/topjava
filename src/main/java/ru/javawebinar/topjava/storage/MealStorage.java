package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;

import java.util.List;

public interface MealStorage {

    Meal get(Integer uuid);

    void save(Meal meal);

    void update(Meal meal);

    List<Meal> getAll();

    void delete(Integer uuid);

}
