package ru.javawebinar.topjava;

import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.javawebinar.topjava.web.json.JsonUtil;

import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestUtil {
    public static String getContent(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString();
    }

    public static <T> T readFromJson(ResultActions action, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtil.readValue(getContent(action.andReturn()), clazz);
    }

    public static <T> T readFromJsonMvcResult(MvcResult result, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtil.readValue(getContent(result), clazz);
    }

    public static <T> List<T> readListFromJsonMvcResult(MvcResult result, Class<T> clazz) throws UnsupportedEncodingException {
        return JsonUtil.readValues(getContent(result), clazz);
    }

    public static <T> void assertMatch(T actual, T expected) {
        assertThat(actual).isEqualTo(expected);
    }

    public static <T> void assertMatch(T actual, T expected, String... ignoreProperties) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, ignoreProperties);
    }

    public static <T> void assertMatch(Iterable<T> actual, Iterable<T> expected, String... ignoreProperties) {
        assertThat(actual).usingElementComparatorIgnoringFields(ignoreProperties).isEqualTo(expected);
    }

    public static <T> ResultMatcher fromJsonAndAssert(T expected, Class<T> clazz) {
        return result -> assertMatch(readFromJsonMvcResult(result, clazz), expected);
    }

    public static <T> ResultMatcher fromJsonAndAssert(T expected, Class<T> clazz, String... ignoreProperties) {
        return result -> assertMatch(readFromJsonMvcResult(result, clazz), expected, ignoreProperties);
    }

    public static <T> ResultMatcher fromJsonAndAssert(Iterable<T> expected, Class<T> clazz) {
        return result -> assertMatch(readListFromJsonMvcResult(result, clazz), expected);
    }

    public static <T> ResultMatcher fromJsonAndAssert(Iterable<T> expected, Class<T> clazz, String... ignoreProperties) {
        return result -> assertMatch(readListFromJsonMvcResult(result, clazz), expected, ignoreProperties);
    }
}
