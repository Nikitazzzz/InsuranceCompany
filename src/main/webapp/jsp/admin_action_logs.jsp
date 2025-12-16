<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Лог действий</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.2">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="app">
    <div style="margin-bottom: 16px;">
        <a href="${pageContext.request.contextPath}/admin/home" class="btn btn-ghost">← На главную</a>
    </div>
    
    <div class="card">
        <h3>Лог действий системы</h3>
        <p class="muted">История всех действий пользователей в системе</p>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Дата/Время</th>
                    <th>Пользователь</th>
                    <th>Роль</th>
                    <th>Тип действия</th>
                    <th>Сущность</th>
                    <th>Описание</th>
                    <th>IP адрес</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty logs}">
                        <tr><td colspan="7" class="muted">Нет записей в логе.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="logInfo" items="${logs}">
                            <tr>
                                <td>
                                    <c:if test="${logInfo.log.createdAt != null}">
                                        ${logInfo.log.createdAt}
                                    </c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${logInfo.log.userId != null}">
                                            <c:choose>
                                                <c:when test="${not empty logInfo.userName}">
                                                    <strong>${logInfo.userName}</strong><br>
                                                    <span class="muted" style="font-size:12px;">Логин: ${logInfo.userLogin}</span><br>
                                                    <span class="muted" style="font-size:11px;">ID: ${logInfo.log.userId}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    ID: ${logInfo.log.userId}
                                                </c:otherwise>
                                            </c:choose>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="muted">Система</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <span class="badge
                                        <c:choose>
                                            <c:when test="${logInfo.log.userRole == 'ADMIN'}">badge-primary</c:when>
                                            <c:when test="${logInfo.log.userRole == 'OWNER'}">badge-default</c:when>
                                            <c:otherwise>badge-default</c:otherwise>
                                        </c:choose>
                                    ">${logInfo.log.userRole}</span>
                                </td>
                                <td><strong>${logInfo.log.actionType}</strong></td>
                                <td>${logInfo.log.entityType}</td>
                                <td>${logInfo.log.description}</td>
                                <td class="muted" style="font-size:12px;">${logInfo.log.ipAddress != null ? logInfo.log.ipAddress : '-'}</td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>
</body>
</html>

