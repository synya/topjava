package ru.javawebinar.topjava.web.meal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.to.MealTo;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.exception.NotFoundException;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.ValidationUtil.assureIdConsistent;
import static ru.javawebinar.topjava.util.ValidationUtil.checkNew;

@Controller
public class MealRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private MealService service;

    public List<MealTo> getAll() {
        log.info("getAll");
        return MealsUtil.getWithExcess(service.getAll(SecurityUtil.getAuthUserId()), SecurityUtil.authUserCaloriesPerDay());
    }

    public List<MealTo> getAllFiltered(LocalDate startDate, LocalDate endDate, LocalTime startTime, LocalTime endTime) {
        log.info("getAllFiltered");
        startDate = (startDate == null) ? LocalDate.MIN : startDate;
        endDate = (endDate == null) ? LocalDate.MAX : endDate;
        startTime = (startTime == null) ? LocalTime.MIN : startTime;
        endTime = (endTime == null) ? LocalTime.MAX : endTime;
        return MealsUtil.getFilteredWithExcess(service.getAllFiltered(SecurityUtil.getAuthUserId(), startDate, endDate),
                SecurityUtil.authUserCaloriesPerDay(), startTime, endTime);
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
        checkNew(meal);
        return service.create(meal, SecurityUtil.getAuthUserId());
    }

    public void update(Meal meal, int id) throws NotFoundException {
        log.info("update {} with id={}", meal, id);
        assureIdConsistent(meal, id);
        service.update(meal, SecurityUtil.getAuthUserId());
    }
}