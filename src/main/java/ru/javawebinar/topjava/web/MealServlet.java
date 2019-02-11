package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.storage.MapMealStorage;
import ru.javawebinar.topjava.storage.MealStorage;
import ru.javawebinar.topjava.util.MealTestData;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.util.TimeUtil;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalTime;

import static ru.javawebinar.topjava.util.MealsUtil.getFilteredWithExcess;

public class MealServlet extends HttpServlet {

    private MealStorage mealStorage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mealStorage = new MapMealStorage();
        MealTestData.LIST.forEach(m -> mealStorage.save(m));
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(TimeUtil.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.valueOf(request.getParameter("calories")));
        String id = request.getParameter("id");
        if ((id == null) || (id.length() == 0)) {
            mealStorage.save(meal);
        } else {
            meal.setId(Integer.valueOf(id));
            mealStorage.update(meal);
        }
        response.sendRedirect("meals");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        switch (String.valueOf(request.getParameter("action"))) {
            case "add":
                request.setAttribute("meal", MealsUtil.EMPTY);
                request.getRequestDispatcher("/editmeal.jsp").forward(request, response);
                break;
            case "edit":
                request.setAttribute("meal", mealStorage.get(Integer.valueOf(id)));
                request.getRequestDispatcher("/editmeal.jsp").forward(request, response);
                break;
            case "delete":
                mealStorage.delete(Integer.valueOf(id));
                response.sendRedirect("meals");
                break;
            default:
                request.setAttribute("mealsTo", getFilteredWithExcess(mealStorage.getAll(), LocalTime.MIN, LocalTime.MAX,
                        MealTestData.CALORIES_PER_DAY_LIMIT));
                request.getRequestDispatcher("/meals.jsp").forward(request, response);
                break;
        }
    }
}
