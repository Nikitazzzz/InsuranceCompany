<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Регистрация</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.1">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="register-container">
    <div class="register-card card">
        <h3>Регистрация</h3>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/register">
            <div class="form-row">
                <div class="form-group">
                    <label for="login">Логин</label>
                    <input id="login" name="login" required type="text"/>
                </div>
                <div class="form-group">
                    <label for="password">Пароль</label>
                    <input id="password" name="password" required type="password"/>
                </div>
            </div>

            <h3 style="margin-top:18px">Личные данные</h3>
            <div class="form-row">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input id="email" name="email" type="email"/>
                </div>
                <div class="form-group">
                    <label for="phone">Телефон</label>
                    <input id="phone" name="phone" type="text"/>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="oName">Имя</label>
                    <input id="oName" name="oName" type="text"/>
                </div>
                <div class="form-group">
                    <label for="surname">Фамилия</label>
                    <input id="surname" name="surname" type="text"/>
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="middleName">Отчество</label>
                    <input id="middleName" name="middleName" type="text"/>
                </div>
                <div class="form-group">
                    <label for="birthday">Дата рождения</label>
                    <input id="birthday" name="birthday" type="date"/>
                </div>
            </div>

            <div class="form-group">
                <label for="driverExp">Стаж вождения (лет)</label>
                <input id="driverExp" name="driverExp" type="number" min="0" value="0"/>
            </div>

            <div style="display:flex;gap:10px;">
                <button class="btn btn-primary" type="submit">Зарегистрироваться</button>
                <a class="btn btn-ghost" href="${pageContext.request.contextPath}/login">Уже есть аккаунт?</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>
</body>
</html>
