package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.ComparisonUtil;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public List<MealTo> getAll() {
        log.info("getAll");
        return getAll(LocalDate.MIN, LocalDate.MAX, LocalTime.MIN, LocalTime.MAX);
    }

    public List<MealTo> getAll(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        return MealsUtil.getWithExcess(service.getAll(SecurityUtil.getAuthUserId(), startDate, endDate), SecurityUtil.authUserCaloriesPerDay()).stream()
                .filter(m -> ComparisonUtil.isBetween(m.getDateTime().toLocalTime(), startTime, endTime))
                .collect(Collectors.toList());
    }

    public Meal get(int id) throws NotFoundException {
        log.info("get {}", id);
        return service.get(id, SecurityUtil.getAuthUserId());
    }

    public void delete(int id) throws NotFoundException {
        log.info("delete {}", id);
        service.delete(id, SecurityUtil.getAuthUserId());
    }

    public Meal create(Meal meal) {
        log.info("create {}", meal);
        return service.create(meal, SecurityUtil.getAuthUserId());
    }

    public void update(Meal meal, int id) throws NotFoundException {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal, SecurityUtil.getAuthUserId());
    }
}