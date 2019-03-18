package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
@RequestMapping(value = "/meals")
public class JspMealController extends AbstractMealController {
    @GetMapping(params = {"action=delete", "id"})
    public String delete(@RequestParam(value = "id") Integer id) {
        super.delete(id);
        return "redirect:meals";
    }

    @GetMapping(params = "action=create")
    public String create(Model model) {
        model.addAttribute("meal",
                new Meal(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "", 1000));
        return "mealForm";
    }

    @GetMapping(params = {"action=update", "id"})
    public String update(@RequestParam(value = "id") Integer id, Model model) {
        model.addAttribute("meal",
                super.get(id));
        return "mealForm";
    }

    @GetMapping
    public String getAll(Model model) {
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @PostMapping(params = "action=filter")
    public String getBetween(@RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate,
                             @RequestParam("startTime") String startTime, @RequestParam("endTime") String endTime,
                             Model model) {
        model.addAttribute("meals", super.getBetween(parseLocalDate(startDate), parseLocalTime(startTime),
                parseLocalDate(endDate), parseLocalTime(endTime)));
        return "meals";
    }

    @PostMapping()
    public String persist(@RequestParam("dateTime") String dateTime, @RequestParam("description") String description,
                          @RequestParam("calories") String calories, @RequestParam("id") String id) {
        Meal meal = new Meal(LocalDateTime.parse(dateTime), description, Integer.parseInt(calories));
        if (StringUtils.isEmpty(id)) {
            super.create(meal);
        } else {
            super.update(meal, Integer.valueOf(id));
        }
        return "redirect:meals";
    }
}
