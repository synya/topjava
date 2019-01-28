package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );
        System.out.println(getFilteredWithExceededLoops(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(getFilteredWithExceededStreams(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(getFilteredWithExceededStreams2(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
        System.out.println(getFilteredWithExceededStreams3(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000));
    }

    public static List<UserMealWithExceed> getFilteredWithExceededLoops(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        mealList.forEach(meal -> caloriesPerDayMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum));
        List<UserMealWithExceed> resultList = new ArrayList<>();
        mealList.forEach(meal -> {
            if (TimeUtil.isBetween(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                resultList.add(new UserMealWithExceed(meal, caloriesPerDayMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        });
        return resultList;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStreams(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = mealList.stream()
                .collect(Collectors.toMap(meal -> meal.getDateTime().toLocalDate(), UserMeal::getCalories, Integer::sum));
        return mealList.stream()
                .filter(meal -> TimeUtil.isBetween(meal.getDateTime().toLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExceed(meal, caloriesPerDayMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStreams2(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return mealList.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate()))
                .values()
                .stream()
                .map(inputList -> {
                    int totalCalories = inputList.stream()
                            .mapToInt(UserMeal::getCalories)
                            .sum();
                    return inputList.stream()
                            .filter(meal -> TimeUtil.isBetween(meal.getDateTime().toLocalTime(), startTime, endTime))
                            .map(meal -> new UserMealWithExceed(meal, totalCalories > caloriesPerDay))
                            .collect(Collectors.toList());
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStreams3(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        class UserMealCollector implements Collector<UserMeal, List<UserMeal>, List<UserMealWithExceed>> {

            private Map<LocalDate, Integer> caloriesPerDayMap = new ConcurrentHashMap<>();

            @Override
            public Supplier<List<UserMeal>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<UserMeal>, UserMeal> accumulator() {
                return (list, meal) -> {
                    caloriesPerDayMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), Integer::sum);
                    if (TimeUtil.isBetween(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                        list.add(meal);
                    }
                };
            }

            @Override
            public BinaryOperator<List<UserMeal>> combiner() {
                return (list1, list2) -> {
                    list1.addAll(list2);
                    return list1;
                };
            }

            @Override
            public Function<List<UserMeal>, List<UserMealWithExceed>> finisher() {
                return (list) -> {
                    List<UserMealWithExceed> resultList = new ArrayList<>();
                    list.forEach(meal -> resultList.add(new UserMealWithExceed(meal, caloriesPerDayMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)));
                    return resultList;
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        }
        return mealList.stream()
                .collect(Collectors.groupingBy(meal -> meal.getDateTime().toLocalDate(), new UserMealCollector()))
                .values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
