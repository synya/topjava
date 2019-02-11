<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="ru.javawebinar.topjava.util.TimeUtil" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Список еды</title>
</head>
<body>
<h3><a href="index.html">Home</a></h3>
<h2>Список еды:</h2>
<table border="1" cellpadding="8" cellspacing="0">
    <tr>
        <th>ID</th>
        <th>Дата/время</th>
        <th>Описание</th>
        <th>Калории</th>
        <th></th>
        <th></th>
    </tr>
    <c:forEach items="${mealsTo}" var="mealTo">
        <jsp:useBean id="mealTo" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr bgcolor="<c:out value="${mealTo.excess ? 'Red' : 'Green'}" />">
            <td>${mealTo.id}</td>
            <td>${TimeUtil.format(mealTo.dateTime)}</td>
            <td>${mealTo.description}</td>
            <td>${mealTo.calories}</td>
            <td><a href="meals?id=${mealTo.id}&action=edit">редактировать</a></td>
            <td><a href="meals?id=${mealTo.id}&action=delete">удалить</a></td>
        </tr>
    </c:forEach>
</table>
<a href="meals?action=add">добавить новую запись</a>
</body>
</html>