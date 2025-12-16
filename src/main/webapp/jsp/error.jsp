<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.1">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="app" style="min-height:50vh;display:flex;align-items:center;justify-content:center">
    <div class="card" style="text-align:center;max-width:700px">
        <h3>Произошла ошибка</h3>
        <c:choose>
            <c:when test="${not empty pageContext.errorData and pageContext.errorData.statusCode != 0}">
                <p class="muted">Код ошибки: <strong>${pageContext.errorData.statusCode}</strong></p>
            </c:when>
            <c:when test="${not empty requestScope.error}">
                <p class="muted">${requestScope.error}</p>
            </c:when>
            <c:otherwise>
                <p class="muted">Произошла неизвестная ошибка</p>
            </c:otherwise>
        </c:choose>
        <p style="margin-top: 16px;">
            <a class="btn btn-ghost" href="${pageContext.request.contextPath}/login">Вернуться на главную</a>
        </p>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>
</body>
</html>
