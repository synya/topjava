package ru.javawebinar.topjava;

import ru.javawebinar.topjava.model.Meal;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.model.AbstractBaseEntity.START_SEQ;

public class MealTestData {

    public static final Meal USER_MEAL1 = new Meal(START_SEQ + 2, LocalDateTime.of(2015, Month.MAY, 28, 10, 0), "Завтрак", 500);
    public static final Meal USER_MEAL2 = new Meal(START_SEQ + 3, LocalDateTime.of(2015, Month.MAY, 28, 13, 0), "Обед", 1000);
    public static final Meal USER_MEAL3 = new Meal(START_SEQ + 4, LocalDateTime.of(2015, Month.MAY, 28, 20, 0), "Ужин", 500);
    public static final Meal USER_MEAL4 = new Meal(START_SEQ + 5, LocalDateTime.of(2015, Month.MAY, 29, 10, 0), "Завтрак", 500);
    public static final Meal USER_MEAL5 = new Meal(START_SEQ + 6, LocalDateTime.of(2015, Month.MAY, 29, 13, 0), "Обед", 1000);
    public static final Meal USER_MEAL6 = new Meal(START_SEQ + 7, LocalDateTime.of(2015, Month.MAY, 29, 20, 0), "Ужин", 500);
    public static final Meal USER_MEAL7 = new Meal(START_SEQ + 8, LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500);
    public static final Meal USER_MEAL8 = new Meal(START_SEQ + 9, LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000);
    public static final Meal USER_MEAL9 = new Meal(START_SEQ + 10, LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500);
    public static final Meal USER_MEAL10 = new Meal(START_SEQ + 11, LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000);
    public static final Meal USER_MEAL11 = new Meal(START_SEQ + 12, LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500);
    public static final Meal USER_MEAL12 = new Meal(START_SEQ + 13, LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510);

    public static final Meal ADMIN_MEAL1 = new Meal(START_SEQ + 14, LocalDateTime.of(2015, Month.JUNE, 01, 14, 0), "Админ ланч", 500);
    public static final Meal ADMIN_MEAL2 = new Meal(START_SEQ + 15, LocalDateTime.of(2015, Month.JUNE, 01, 21, 0), "Админ ужин", 1000);

    public static void assertMatch(Meal actual, Meal expected) {
        assertThat(actual).isEqualToComparingFieldByField(expected);
    }

    public static void assertMatch(Iterable<Meal> actual, Meal... meals) {
        assertThat(actual).usingFieldByFieldElementComparator().isEqualTo(Arrays.asList(meals));
    }

}
