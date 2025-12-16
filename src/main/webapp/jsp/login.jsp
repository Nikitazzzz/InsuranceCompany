<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Вход в систему</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.1">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="login-container">
    <div class="login-card card">
        <h3>Вход в систему</h3>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/login">
            <div class="form-group">
                <label for="login">Логин</label>
                <input id="login" name="login" required type="text"/>
            </div>
            <div class="form-group">
                <label for="password">Пароль</label>
                <input id="password" name="password" required type="password"/>
            </div>
            <div style="display:flex;gap:8px;align-items:center;">
                <button class="btn btn-primary" type="submit">Войти</button>
                <a class="btn btn-ghost" href="${pageContext.request.contextPath}/register">Регистрация</a>
            </div>
        </form>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>
</body>
</html>
