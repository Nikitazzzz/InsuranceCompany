<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Управление администраторами</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.2">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="app">
    <div style="margin-bottom: 16px;">
        <a href="${pageContext.request.contextPath}/admin/home" class="btn btn-ghost">← На главную</a>
    </div>

    <c:if test="${not empty error}">
        <div class="card" style="background-color: var(--danger-light); border-color: var(--danger); margin-bottom: 16px;">
            <p style="color: var(--danger); margin: 0;">${error}</p>
        </div>
    </c:if>

    <div class="card">
        <h3>Добавить администратора</h3>
        <form method="post" action="${pageContext.request.contextPath}/admin/administrators">
            <input type="hidden" name="action" value="create"/>
            
            <div class="form-group">
                <label>Имя</label>
                <input type="text" name="aName" required/>
            </div>
            
            <div class="form-group">
                <label>Фамилия</label>
                <input type="text" name="surname" required/>
            </div>
            
            <div class="form-group">
                <label>Должность</label>
                <input type="text" name="position" required/>
            </div>
            
            <div class="form-group">
                <label>Опыт работы (лет)</label>
                <input type="number" name="workExp" min="0" value="0" required/>
            </div>
            
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email"/>
            </div>
            
            <div class="form-group">
                <label>Логин</label>
                <input type="text" name="login" required/>
            </div>
            
            <div class="form-group">
                <label>Пароль</label>
                <input type="password" name="password" required/>
            </div>

            <div style="display:flex;gap:10px">
                <button class="btn btn-primary" type="submit">Добавить</button>
                <a class="btn btn-ghost" href="${pageContext.request.contextPath}/admin/home">Отмена</a>
            </div>
        </form>
    </div>

    <div class="card">
        <h3>Список администраторов</h3>
        <div class="table-wrap">
            <table>
                <thead>
                <tr><th>ID</th><th>Имя</th><th>Фамилия</th><th>Должность</th><th>Опыт</th><th>Email</th><th>Действия</th></tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty administrators}">
                        <tr>
                            <td colspan="7" class="muted" style="text-align:center;padding:24px;">
                                Администраторы не найдены
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="admin" items="${administrators}">
                            <tr>
                                <td>${admin.id}</td>
                                <td>${admin.name}</td>
                                <td>${admin.surname}</td>
                                <td>${admin.position}</td>
                                <td>${admin.workExp}</td>
                                <td>${admin.email != null ? admin.email : '-'}</td>
                                <td>
                                    <form method="post" style="display:inline;" class="table-inline-form delete-form" data-admin-name="${admin.name} ${admin.surname}">
                                        <input type="hidden" name="action" value="delete"/>
                                        <input type="hidden" name="id" value="${admin.id}"/>
                                        <button class="btn btn-ghost" type="button">Удалить</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<!-- Модальное окно подтверждения -->
<div id="confirmModal" class="confirm-modal">
    <div class="confirm-modal-content">
        <h3 id="confirmModalTitle">Подтверждение действия</h3>
        <p id="confirmModalMessage"></p>
        <div class="confirm-modal-actions">
            <button class="btn btn-ghost" id="confirmModalCancel">Отмена</button>
            <button class="btn btn-primary" id="confirmModalConfirm">Подтвердить</button>
        </div>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>

<script>
// Подтверждение удаления администратора
document.addEventListener('DOMContentLoaded', function() {
    const confirmModal = document.getElementById('confirmModal');
    const confirmModalTitle = document.getElementById('confirmModalTitle');
    const confirmModalMessage = document.getElementById('confirmModalMessage');
    const confirmModalCancel = document.getElementById('confirmModalCancel');
    const confirmModalConfirm = document.getElementById('confirmModalConfirm');
    let formToSubmit = null;
    
    function showConfirmModal(title, message, callback) {
        confirmModalTitle.textContent = title;
        confirmModalMessage.textContent = message;
        confirmModal.classList.add('active');
        
        const handleConfirm = () => {
            confirmModal.classList.remove('active');
            if (callback) callback();
            confirmModalConfirm.removeEventListener('click', handleConfirm);
        };
        
        const handleCancel = () => {
            confirmModal.classList.remove('active');
            formToSubmit = null;
            confirmModalCancel.removeEventListener('click', handleCancel);
        };
        
        confirmModalConfirm.addEventListener('click', handleConfirm);
        confirmModalCancel.addEventListener('click', handleCancel);
        
        confirmModal.addEventListener('click', function(e) {
            if (e.target === confirmModal) {
                handleCancel();
            }
        });
    }
    
    const deleteForms = document.querySelectorAll('form.delete-form');
    deleteForms.forEach(form => {
        const submitBtn = form.querySelector('button[type="button"]');
        if (submitBtn) {
            submitBtn.addEventListener('click', function(e) {
                e.preventDefault();
                const adminName = form.getAttribute('data-admin-name') || 'этого администратора';
                formToSubmit = form;
                showConfirmModal(
                    'Подтверждение удаления',
                    'Вы уверены, что хотите удалить администратора ' + adminName + '?',
                    () => {
                        if (formToSubmit) {
                            formToSubmit.submit();
                        }
                    }
                );
            });
        }
    });
});
</script>
</body>
</html>

