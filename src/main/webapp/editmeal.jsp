<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Редактор еды</title>
    <jsp:useBean id="meal" type="ru.javawebinar.topjava.model.Meal" scope="request"/>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<form method="post" action="meals" enctype="application/x-www-form-urlencoded">
    <input type="hidden" name="id" value="${meal.id}">
    <input type="text" name="dateTime" size=20 value="${TimeUtil.format(meal.dateTime)}" pattern="[0-9]{2}.[0-9]{2}.[0-9]{2} [0-9]{2}:[0-9]{2}">
    <input type="text" name="description" size=20 value="${meal.description}">
    <input type="number" name="calories" size=20 value="${meal.calories}" min="0">
    <button type="submit">Сохранить</button>
</form>
<button onclick="window.history.back()">Отменить</button>
</body>
</html>