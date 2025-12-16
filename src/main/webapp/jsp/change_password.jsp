<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Смена пароля</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.1">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="app">
    <div style="margin-bottom: 16px;">
        <a href="${pageContext.request.contextPath}/user/home" class="btn btn-ghost">← На главную</a>
    </div>
    
    <div class="card">
        <h3>Смена пароля</h3>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>
        
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        
        <form method="post" action="${pageContext.request.contextPath}/user/change-password">
            <div class="form-group">
                <label>Текущий пароль</label>
                <input type="password" name="currentPassword" required/>
            </div>
            
            <div class="form-group">
                <label>Новый пароль</label>
                <input type="password" name="newPassword" required minlength="6"/>
                <div class="muted" style="margin-top:8px;font-size:13px">
                    Минимальная длина пароля: 6 символов
                </div>
            </div>
            
            <div class="form-group">
                <label>Подтверждение нового пароля</label>
                <input type="password" name="confirmPassword" required minlength="6"/>
            </div>
            
            <button class="btn btn-primary" type="submit">Изменить пароль</button>
        </form>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>
</body>
</html>

