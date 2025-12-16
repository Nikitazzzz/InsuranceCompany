<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Панель администратора</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.1">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="app">
    <div class="card">
        <h3>Панель администратора</h3>
        <p class="muted" style="margin-bottom: 24px;">Быстрый доступ к основным разделам</p>

        <div class="main-nav">
            <a href="${pageContext.request.contextPath}/admin/tariffs" class="nav-card">
                <h4>📊 Управление тарифами</h4>
                <p>Создание и редактирование тарифов страхования</p>
            </a>

            <a href="${pageContext.request.contextPath}/admin/insurances" class="nav-card">
                <h4>⚠️ Страховые случаи</h4>
                <p>Просмотр и обработка заявок на страховые случаи</p>
            </a>

            <a href="${pageContext.request.contextPath}/admin/payouts" class="nav-card">
                <h4>💸 Выплаты</h4>
                <p>Создание и управление выплатами по страховым случаям</p>
            </a>

            <a href="${pageContext.request.contextPath}/admin/policy-requests" class="nav-card">
                <h4>📋 Заявки на полисы</h4>
                <p>Просмотр и подтверждение заявок на оформление полисов</p>
            </a>

            <a href="${pageContext.request.contextPath}/admin/policies" class="nav-card">
                <h4>📑 Управление полисами</h4>
                <p>Просмотр всех полисов, поиск, продление и аннулирование</p>
            </a>

            <c:if test="${not empty sessionScope.currentUser && sessionScope.currentUser.role == 'ADMIN' && sessionScope.currentUser.adminId == 1}">
                <a href="${pageContext.request.contextPath}/admin/administrators" class="nav-card">
                    <h4>👥 Управление администраторами</h4>
                    <p>Добавление и управление администраторами системы</p>
                </a>
                
                <a href="${pageContext.request.contextPath}/admin/action-logs" class="nav-card">
                    <h4>📝 Лог действий</h4>
                    <p>Просмотр истории всех действий в системе</p>
                </a>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>
</body>
</html>
