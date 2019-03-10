package ru.javawebinar.topjava.service.datajpa;

import org.junit.Test;
import org.springframework.test.context.ActiveProfiles;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.Profiles;
import ru.javawebinar.topjava.UserTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.service.AbstractUserServiceTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.UserTestData.*;

@ActiveProfiles({Profiles.DATAJPA})
public class DataJpaUserServiceTest extends AbstractUserServiceTest {
    @Test
    public void getUserWithMealsTest() {
        User user = service.getWithMeals(ADMIN_ID);
        UserTestData.assertMatch(user, ADMIN);
        MealTestData.assertMatch(user.getMeals(), MealTestData.ADMIN_MEAL2, MealTestData.ADMIN_MEAL1);
    }

    @Test
    public void getUserWithNoMealsTest() {
        List<Meal> meals = service.getWithMeals(USER_WITH_NO_MEALS_ID).getMeals();
        assertThat(meals).hasSize(0);
    }
}
