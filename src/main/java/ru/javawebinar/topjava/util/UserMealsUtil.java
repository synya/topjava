package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.stream.Collector.Characteristics.CONCURRENT;

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
        List<UserMeal> filteredMealList = new ArrayList<>();
        mealList.forEach(m -> {
            caloriesPerDayMap.merge(m.getDateTime().toLocalDate(), m.getCalories(), (oldCal, newCal) -> oldCal + newCal);
            if (TimeUtil.isBetween(m.getDateTime().toLocalTime(), startTime, endTime)) {
                filteredMealList.add(m);
            }
        });
        return toUserMealWithExceedList(filteredMealList, caloriesPerDayMap, caloriesPerDay);
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStreams(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesPerDayMap = mealList.stream()
                .collect(Collectors.toMap(m -> m.getDateTime().toLocalDate(), UserMeal::getCalories, (oldCal, newCal) -> oldCal + newCal));
        return mealList.stream()
                .filter(m -> TimeUtil.isBetween(m.getDateTime().toLocalTime(), startTime, endTime))
                .map(m -> new UserMealWithExceed(m, caloriesPerDayMap.get(m.getDateTime().toLocalDate()) > caloriesPerDay))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStreams2(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return mealList.stream()
                .collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate()))
                .values()
                .stream()
                .map(inputList -> {
                    AtomicInteger totalCalories = new AtomicInteger();
                    inputList.forEach(m -> totalCalories.addAndGet(m.getCalories()));
                    List<UserMealWithExceed> outputList = new ArrayList<>();
                    inputList.forEach(m -> outputList.add(new UserMealWithExceed(m, totalCalories.get() > caloriesPerDay)));
                    return outputList;
                })
                .flatMap(Collection::stream)
                .filter(m -> TimeUtil.isBetween(m.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceededStreams3(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        return mealList.stream()
                .collect(Collectors.groupingBy(m -> m.getDateTime().toLocalDate(), new UserMealCollector(caloriesPerDay)))
                .values()
                .stream()
                .flatMap(Collection::stream)
                .filter(m -> TimeUtil.isBetween(m.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    private static class UserMealCollector implements Collector<UserMeal, List<UserMeal>, List<UserMealWithExceed>> {

        private Map<LocalDate, Integer> caloriesPerDayMap = new HashMap<>();
        private int caloriesPerDay;

        public UserMealCollector(int caloriesPerDay) {
            this.caloriesPerDay = caloriesPerDay;
        }

        @Override
        public Supplier<List<UserMeal>> supplier() {
            return ArrayList::new;
        }

        @Override
        public BiConsumer<List<UserMeal>, UserMeal> accumulator() {
            return (list, meal) -> {
                caloriesPerDayMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), (oldCal, newCal) -> oldCal + newCal);
                list.add(meal);
            };
        }

        @Override
        public BinaryOperator<List<UserMeal>> combiner() {
            return (list1, list2) -> {
                list2.forEach(meal -> caloriesPerDayMap.merge(meal.getDateTime().toLocalDate(), meal.getCalories(), (oldCal, newCal) -> oldCal + newCal));
                list1.addAll(list2);
                return list1;
            };
        }

        @Override
        public Function<List<UserMeal>, List<UserMealWithExceed>> finisher() {
            return (accumulatedList) -> toUserMealWithExceedList(accumulatedList, caloriesPerDayMap, caloriesPerDay);
        }

        @Override
        public Set<Characteristics> characteristics() {
            return EnumSet.of(CONCURRENT);
        }
    }

    private static List<UserMealWithExceed> toUserMealWithExceedList(List<UserMeal> userMeals, Map<LocalDate, Integer> caloriesPerDayMap, int caloriesPerDay) {
        List<UserMealWithExceed> resultList = new ArrayList<>();
        userMeals.forEach(meal -> resultList.add(new UserMealWithExceed(meal, caloriesPerDayMap.get(meal.getDateTime().toLocalDate()) > caloriesPerDay)));
        return resultList;
    }

}
