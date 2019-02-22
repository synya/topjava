package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.Month;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.ADMIN_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        // Only for postgres driver logging
        // It uses java.util.logging and logged via jul-to-slf4j bridge
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(USER_MEAL1.getId(), USER_ID);
        assertMatch(meal, USER_MEAL1);
    }

    @Test(expected = NotFoundException.class)
    public void getNotBelonging() {
        service.get(USER_MEAL1.getId(), ADMIN_ID);
    }

    @Test
    public void delete() {
        service.delete(USER_MEAL12.getId(), USER_ID);
        assertMatch(service.getAll(USER_ID), USER_MEAL11, USER_MEAL10, USER_MEAL9, USER_MEAL8,
                USER_MEAL7, USER_MEAL6, USER_MEAL5, USER_MEAL4, USER_MEAL3, USER_MEAL2, USER_MEAL1);
    }

    @Test(expected = NotFoundException.class)
    public void deleteNotBelonging() {
        service.get(USER_MEAL1.getId(), ADMIN_ID);
    }

    @Test
    public void getBetweenDateTimes() {
        assertMatch(service.getBetweenDateTimes(LocalDateTime.of(2015, Month.MAY, 29, 0, 0), LocalDateTime.of(2015, Month.MAY, 30, 23, 0), USER_ID), USER_MEAL9, USER_MEAL8,
                USER_MEAL7, USER_MEAL6, USER_MEAL5, USER_MEAL4);
    }

    @Test
    public void getAll() {
        assertMatch(service.getAll(USER_ID), USER_MEAL12, USER_MEAL11, USER_MEAL10, USER_MEAL9, USER_MEAL8,
                USER_MEAL7, USER_MEAL6, USER_MEAL5, USER_MEAL4, USER_MEAL3, USER_MEAL2, USER_MEAL1);
    }

    @Test
    public void update() {
        Meal meal = service.get(USER_MEAL1.getId(), USER_ID);
        meal.setDescription("Обновленное описание еды");
        meal.setCalories(420);
        service.update(meal, USER_ID);
        assertMatch(service.get(USER_MEAL1.getId(), USER_ID), meal);
    }

    @Test(expected = NotFoundException.class)
    public void updateNotBelonging() {
        Meal meal = service.get(USER_MEAL1.getId(), USER_ID);
        meal.setDescription("Обновленное описание еды");
        meal.setCalories(420);
        service.update(meal, ADMIN_ID);
    }

    @Test
    public void create() {
        Meal meal = new Meal(LocalDateTime.of(2015, Month.MAY, 15, 20, 0), "Тест", 1510);
        service.create(meal, USER_ID);
        assertMatch(service.get(START_SEQ + 16, USER_ID), meal);
    }
}