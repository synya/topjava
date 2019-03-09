package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_WITH_NO_MEALS_ID;

@ActiveProfiles({Profiles.DATAJPA})
public class DataJpaUserServiceTest extends AbstractUserServiceTest {
    @Test
    public void getUserWithMealsTest() {
        List<Meal> meals = service.getWithMeals(ADMIN_ID).getMeals();
        MealTestData.assertMatch(meals, MealTestData.ADMIN_MEAL2, MealTestData.ADMIN_MEAL1);
    }
    @Test
    public void getUserWithNoMealsTest() {
        List<Meal> meals = service.getWithMeals(USER_WITH_NO_MEALS_ID).getMeals();
        assertThat(meals).hasSize(0);
    }
}
