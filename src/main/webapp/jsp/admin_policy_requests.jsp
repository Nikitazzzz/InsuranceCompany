<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Заявки на полисы</title>
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
        <h3>Заявки на оформление полисов</h3>
        <p class="muted" style="margin-bottom: 16px;">Заявки, ожидающие подтверждения администратора</p>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Номер заявки</th>
                    <th>Владелец</th>
                    <th>Автомобиль</th>
                    <th>Тариф</th>
                    <th>Период</th>
                    <th>Стоимость</th>
                    <th>Дата создания</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty policyRequests}">
                        <tr>
                            <td colspan="8" class="muted" style="text-align:center;padding:24px;">
                                Нет заявок, ожидающих подтверждения
                            </td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="request" items="${policyRequests}">
                            <tr>
                                <td><strong>${request.policy.policyNumber}</strong></td>
                                <td>${request.owner.name} ${request.owner.surname}</td>
                                <td>${request.vehicle.brand} ${request.vehicle.model}<br>
                                    <span class="muted" style="font-size:12px;">${request.vehicle.reg}</span>
                                </td>
                                <td>${request.tariff.tariffName}<br>
                                    <span class="muted" style="font-size:12px;">${request.tariff.policyType}</span>
                                </td>
                                <td>${request.policy.startDate} - ${request.policy.endDate}</td>
                                <td><strong style="color:var(--success)">${request.policy.price} ₽</strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${request.policy.createdAt != null}">
                                            ${request.policy.createdAt}
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div style="display:flex; gap:8px;">
                                        <form method="post" style="display:inline;" class="table-inline-form approve-form" data-policy-number="${request.policy.policyNumber}">
                                            <input type="hidden" name="action" value="approve"/>
                                            <input type="hidden" name="policyId" value="${request.policy.id}"/>
                                            <button class="btn btn-primary" type="button" style="font-size:12px; padding:4px 8px;">Одобрить</button>
                                        </form>
                                        <form method="post" style="display:inline;" class="table-inline-form reject-form" data-policy-number="${request.policy.policyNumber}">
                                            <input type="hidden" name="action" value="reject"/>
                                            <input type="hidden" name="policyId" value="${request.policy.id}"/>
                                            <button class="btn btn-ghost" type="button" style="font-size:12px; padding:4px 8px;">Отклонить</button>
                                        </form>
                                    </div>
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
// Подтверждение действий с заявками на полисы
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
    
    // Подтверждение одобрения
    const approveForms = document.querySelectorAll('form.approve-form');
    approveForms.forEach(form => {
        const submitBtn = form.querySelector('button[type="button"]');
        if (submitBtn) {
            submitBtn.addEventListener('click', function(e) {
                e.preventDefault();
                const policyNumber = form.getAttribute('data-policy-number') || 'эту заявку';
                formToSubmit = form;
                showConfirmModal(
                    'Подтверждение одобрения',
                    'Вы уверены, что хотите одобрить заявку на полис ' + policyNumber + '?',
                    () => {
                        if (formToSubmit) {
                            formToSubmit.submit();
                        }
                    }
                );
            });
        }
    });
    
    // Подтверждение отклонения
    const rejectForms = document.querySelectorAll('form.reject-form');
    rejectForms.forEach(form => {
        const submitBtn = form.querySelector('button[type="button"]');
        if (submitBtn) {
            submitBtn.addEventListener('click', function(e) {
                e.preventDefault();
                const policyNumber = form.getAttribute('data-policy-number') || 'эту заявку';
                formToSubmit = form;
                showConfirmModal(
                    'Подтверждение отклонения',
                    'Вы уверены, что хотите отклонить заявку на полис ' + policyNumber + '?',
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

