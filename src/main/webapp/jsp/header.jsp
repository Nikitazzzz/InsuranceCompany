<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="header">
    <div class="brand">
        <div class="logo">IF</div>
        <div>
            <h1>InsureFlow</h1>
            <div class="subtitle">Панель управления страхования</div>
        </div>
    </div>
    <div class="actions">
        <c:if test="${not empty sessionScope.currentUser}">
            <div class="muted" style="margin-right:12px">Вошёл как <strong>${sessionScope.currentUser.login}</strong></div>
        </c:if>
        <button class="theme-toggle" type="button" aria-label="Переключить тему" title="Переключить тему">
            <span data-toggle-theme="switch">🌙 Тёмная</span>
        </button>
        <c:if test="${not empty sessionScope.currentUser}">
            <a class="btn btn-ghost" href="${pageContext.request.contextPath}/logout">Выйти</a>
        </c:if>
    </div>
</div>
