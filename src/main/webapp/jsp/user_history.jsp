<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>История операций</title>
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
        <h3>История операций</h3>
        <p class="muted">История всех ваших действий в системе</p>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Дата/Время</th>
                    <th>Тип действия</th>
                    <th>Сущность</th>
                    <th>Описание</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty logs}">
                        <tr><td colspan="4" class="muted">Нет записей в истории.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="log" items="${logs}">
                            <tr>
                                <td>
                                    <c:if test="${log.createdAt != null}">
                                        ${log.createdAt}
                                    </c:if>
                                </td>
                                <td><strong>${log.actionType}</strong></td>
                                <td>${log.entityType}</td>
                                <td>${log.description}</td>
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

