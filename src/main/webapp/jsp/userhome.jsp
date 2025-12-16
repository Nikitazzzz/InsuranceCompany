<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Личный кабинет</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.1">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="app">
    <div class="card">
        <h3>Личный кабинет</h3>
        <c:if test="${not empty sessionScope.currentUser}">
            <div style="margin-bottom: 24px;">
                <h2 style="margin:0; font-size: 28px; font-weight: 400;">${sessionScope.currentUser.login}</h2>
                <div class="muted" style="margin-top: 4px;">Добро пожаловать в систему</div>
            </div>
        </c:if>

        <c:if test="${not empty owner}">
            <div class="card" style="margin-top: 24px;">
                <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom: 20px;">
                    <h3 style="margin:0;">Личная информация</h3>
                    <button onclick="toggleEditForm()" class="btn btn-ghost" id="editBtn">Редактировать</button>
                </div>
                
                <div id="viewMode">
                    <div class="info-grid">
                        <div class="info-item">
                            <div class="info-label">ФИО</div>
                            <div class="info-value">${owner.surname} ${owner.name != null ? owner.name : ''} ${owner.middleName != null ? owner.middleName : ''}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Email</div>
                            <div class="info-value">${owner.email != null && !owner.email.isEmpty() ? owner.email : 'Не указан'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Телефон</div>
                            <div class="info-value">${owner.phone != null && !owner.phone.isEmpty() ? owner.phone : 'Не указан'}</div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Дата рождения</div>
                            <div class="info-value">
                                <c:choose>
                                    <c:when test="${owner.birthday != null}">
                                        ${owner.formattedBirthday}
                                    </c:when>
                                    <c:otherwise>Не указана</c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        <div class="info-item">
                            <div class="info-label">Стаж вождения</div>
                            <div class="info-value">${owner.driverExp} ${owner.driverExp == 1 ? 'год' : owner.driverExp >= 2 && owner.driverExp <= 4 ? 'года' : 'лет'}</div>
                        </div>
                    </div>
                </div>
                
                <div id="editMode" style="display:none;">
                    <form method="post" action="${pageContext.request.contextPath}/user/profile">
                        <input type="hidden" name="action" value="update"/>
                        <div class="form-row">
                            <div class="form-group">
                                <label>Имя</label>
                                <input type="text" name="oName" value="${owner.name}" required/>
                            </div>
                            <div class="form-group">
                                <label>Фамилия</label>
                                <input type="text" name="surname" value="${owner.surname}" required/>
                            </div>
                        </div>
                        <div class="form-group">
                            <label>Отчество</label>
                            <input type="text" name="middleName" value="${owner.middleName != null ? owner.middleName : ''}"/>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>Email</label>
                                <input type="email" name="email" value="${owner.email}"/>
                            </div>
                            <div class="form-group">
                                <label>Телефон</label>
                                <input type="text" name="phone" value="${owner.phone}" required/>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>Дата рождения</label>
                                <input type="date" name="birthday" value="${owner.birthday != null ? owner.birthday : ''}"/>
                            </div>
                            <div class="form-group">
                                <label>Стаж вождения (лет)</label>
                                <input type="number" name="driverExp" value="${owner.driverExp}" min="0" required/>
                            </div>
                        </div>
                        <div style="display:flex; gap:8px; margin-top:16px;">
                            <button type="submit" class="btn btn-primary">Сохранить</button>
                            <button type="button" onclick="toggleEditForm()" class="btn btn-ghost">Отмена</button>
                        </div>
                    </form>
                </div>
            </div>
        </c:if>

        <div class="main-nav">
            <a href="${pageContext.request.contextPath}/user/vehicles" class="nav-card">
                <h4>🚗 Мои автомобили</h4>
                <p>Управление транспортными средствами</p>
            </a>
            <a href="${pageContext.request.contextPath}/user/policies" class="nav-card">
                <h4>📄 Мои полисы</h4>
                <p>Просмотр и оформление страховых полисов</p>
            </a>
            <a href="${pageContext.request.contextPath}/user/insurances" class="nav-card">
                <h4>⚠️ Страховые случаи</h4>
                <p>Подача заявок на страховые случаи</p>
            </a>
            <a href="${pageContext.request.contextPath}/user/history" class="nav-card">
                <h4>📜 История операций</h4>
                <p>Просмотр истории всех ваших действий</p>
            </a>
        </div>
    </div>
    
    <div class="card" style="margin-top: 24px;">
        <h3>Безопасность</h3>
        <a href="${pageContext.request.contextPath}/user/change-password" class="btn btn-ghost">Изменить пароль</a>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>

<script>
function toggleEditForm() {
    const viewMode = document.getElementById('viewMode');
    const editMode = document.getElementById('editMode');
    const editBtn = document.getElementById('editBtn');
    
    if (viewMode.style.display === 'none') {
        viewMode.style.display = 'block';
        editMode.style.display = 'none';
        editBtn.textContent = 'Редактировать';
    } else {
        viewMode.style.display = 'none';
        editMode.style.display = 'block';
        editBtn.textContent = 'Отмена';
    }
}
</script>
</body>
</html>
