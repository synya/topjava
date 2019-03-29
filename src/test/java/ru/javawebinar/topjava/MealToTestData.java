package ru.javawebinar.topjava;

import org.springframework.test.web.servlet.ResultMatcher;
import ru.javawebinar.topjava.to.MealTo;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.javawebinar.topjava.TestUtil.readListFromJsonMvcResult;

public class MealToTestData {
    public static void assertMatch(MealTo actual, MealTo expected) {
        assertThat(actual).isEqualTo(expected);
    }

    public static void assertMatch(Iterable<MealTo> actual, Iterable<MealTo> expected) {
        assertThat(actual).usingDefaultElementComparator().isEqualTo(expected);
    }

    public static ResultMatcher contentJson(Iterable<MealTo> expected) {
        return result -> assertMatch(readListFromJsonMvcResult(result, MealTo.class), expected);
    }
}
