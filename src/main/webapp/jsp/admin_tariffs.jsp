<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Управление тарифами</title>
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
        <h3>Добавить тариф</h3>
                <form method="post" action="${pageContext.request.contextPath}/admin/tariffs">
                    <input type="hidden" name="action" value="create"/>
                    <div class="form-group">
                        <label>ID статуса</label>
                        <input type="number" name="statId" value="1" required/>
                    </div>
                    <div class="form-group">
                        <label>Название тарифа</label>
                        <input type="text" name="tariffName" required/>
                    </div>
                    <div class="form-group">
                        <label>Тип полиса</label>
                        <select name="policyType" required>
                            <option>ОСАГО</option>
                            <option>КАСКО</option>
                            <option>Зелёная карта</option>
                            <option>Страховка водителя и пассажиров</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Описание</label>
                        <textarea name="description" rows="3" placeholder="Описание тарифа"></textarea>
                    </div>
                    <div class="form-group">
                        <label>Базовая цена (₽/мес)</label>
                        <input type="number" name="basePrice" step="0.01" required placeholder="0.00"/>
                        <div class="muted" style="margin-top:8px;font-size:13px">
                            Цена указывается за 1 месяц; итог зависит от периода, стажа и мощности.
                        </div>
                    </div>

                    <div style="display:flex;gap:10px">
                        <button class="btn btn-primary" type="submit">Добавить</button>
                        <a class="btn btn-ghost" href="${pageContext.request.contextPath}/admin/home">Отмена</a>
                    </div>
                </form>
    </div>

    <div class="card">
        <h3>Список тарифов</h3>

                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr><th>ID</th><th>Название</th><th>Тип</th><th>Базовая цена</th><th>Действия</th></tr>
                        </thead>
                        <tbody>
                        <c:forEach var="tariff" items="${tariffs}">
                            <tr>
                                <td>${tariff.id}</td>
                                <td>${tariff.tariffName}</td>
                                <td>${tariff.policyType}</td>
                                <td>${tariff.basePrice} ₽/мес</td>
                                <td>
                                    <form method="post" style="display:inline;" class="table-inline-form delete-form" data-tariff-name="${tariff.tariffName}">
                                        <input type="hidden" name="action" value="delete"/>
                                        <input type="hidden" name="id" value="${tariff.id}"/>
                                        <button class="btn btn-ghost" type="button">Удалить</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
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
// Подтверждение удаления тарифа
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
                const tariffName = form.getAttribute('data-tariff-name') || 'этот тариф';
                formToSubmit = form;
                showConfirmModal(
                    'Подтверждение удаления',
                    'Вы уверены, что хотите удалить тариф "' + tariffName + '"?',
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
