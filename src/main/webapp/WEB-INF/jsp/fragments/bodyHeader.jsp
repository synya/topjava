<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<nav class="navbar navbar-expand-md navbar-dark bg-dark py-0">
    <div class="container">
        <a href="meals" class="navbar-brand"><img src="resources/images/icon-meal.png"> <spring:message
                code="app.title"/></a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExampleDefault"
                aria-controls="navbarsExampleDefault" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navBar">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle my-2" href="#" id="language" data-toggle="dropdown"
                       aria-haspopup="true" aria-expanded="false">${pageContext.response.locale}</a>
                    <div class="dropdown-menu" aria-labelledby="language">
                        <%--                        https://stackoverflow.com/questions/9711896/how-to-get-correct-current-url-in-jsp-in-spring-webapp--%>
                        <a class="dropdown-item"
                           href="${requestScope['javax.servlet.forward.request_uri']}?language=en">English</a>
                        <a class="dropdown-item"
                           href="${requestScope['javax.servlet.forward.request_uri']}?language=ru">Руссккий</a>
                    </div>
                </li>
                <li class="nav-item">
                    <sec:authorize access="isAuthenticated()">
                        <form:form class="form-inline my-2" action="logout" method="post">
                            <sec:authorize access="hasRole('ROLE_ADMIN')">
                                <a class="btn btn-info mr-1" href="users"><spring:message code="user.title"/></a>
                            </sec:authorize>
                            <a class="btn btn-info mr-1" href="profile">${userTo.name} <spring:message
                                    code="app.profile"/></a>
                            <button class="btn btn-primary" type="submit">
                                <span class="fa fa-sign-out"></span>
                            </button>
                        </form:form>
                    </sec:authorize>
                    <sec:authorize access="isAnonymous()">
                        <form:form class="form-inline my-2" id="login_form" action="spring_security_check"
                                   method="post">
                            <input class="form-control mr-1" type="text" placeholder="Email" name="username">
                            <input class="form-control mr-1" type="password" placeholder="Password" name="password">
                            <button class="btn btn-success" type="submit">
                                <span class="fa fa-sign-in"></span>
                            </button>
                        </form:form>
                    </sec:authorize>
                </li>
            </ul>
        </div>
    </div>
</nav>