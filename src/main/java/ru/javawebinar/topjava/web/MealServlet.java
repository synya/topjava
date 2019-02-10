package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
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
        String uuid = request.getParameter("uuid");
        if ((uuid == null) || (uuid.length() == 0)) {
            mealStorage.save(meal);
        } else {
            meal.setUuid(Integer.valueOf(uuid));
            mealStorage.update(meal);
        }
        response.sendRedirect("meals");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uuid = request.getParameter("uuid");
        String action = request.getParameter("action");
        switch (String.valueOf(action)) {
            case "add":
                request.setAttribute("meal", MealsUtil.EMPTY);
                request.getRequestDispatcher("/editmeal.jsp").forward(request, response);
                break;
            case "edit":
                request.setAttribute("meal", mealStorage.get(Integer.valueOf(uuid)));
                request.getRequestDispatcher("/editmeal.jsp").forward(request, response);
                break;
            case "delete":
                mealStorage.delete(Integer.valueOf(uuid));
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
