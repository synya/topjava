package ru.javawebinar.topjava.repository.datajpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
public interface CrudMealRepository extends JpaRepository<Meal, Integer> {
    @Transactional
    int deleteByIdAndUserId(int id, int userId);

    @Override
    @Transactional
    Meal save(Meal meal);

    @Query("SELECT m FROM Meal m JOIN FETCH m.user WHERE m.id=:id and m.user.id=:userId")
    Meal getByIdAndUserIdJoined(@Param("id") Integer id, @Param("userId") Integer userId);

    Meal findByIdAndUserId(Integer id, Integer userId);

    List<Meal> findAllByUserIdOrderByDateTimeDesc(int userId);

    List<Meal> findAllByUserIdAndDateTimeBetweenOrderByDateTimeDesc(int userId, LocalDateTime startDate, LocalDateTime endDate);
}
