package ru.javawebinar.topjava;

import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

public class MealToTestData {
    public static final List<MealTo> MEAL_TOS = MealsUtil.getWithExcess(MealTestData.MEALS, UserTestData.USER.getCaloriesPerDay());

    public static void assertMatch(Iterable<MealTo> actual, Iterable<MealTo> expected) {
        assertThat(actual).usingDefaultElementComparator().isEqualTo(expected);
    }
}
