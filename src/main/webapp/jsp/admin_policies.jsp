<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Управление полисами</title>
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
        <h3>Управление полисами</h3>
        
        <form method="get" action="${pageContext.request.contextPath}/admin/policies" style="margin-bottom: 16px;">
            <div class="form-row">
                <div class="form-group" style="flex:1;">
                    <input type="text" name="search" placeholder="Поиск по номеру полиса..." value="${param.search}"/>
                </div>
                <button type="submit" class="btn btn-primary">Поиск</button>
                <a href="${pageContext.request.contextPath}/admin/policies" class="btn btn-ghost">Сбросить</a>
            </div>
        </form>
        
        <c:if test="${not empty error}">
            <div class="alert alert-error">${error}</div>
        </c:if>

        <div class="table-wrap">
            <table>
                <thead>
                <tr>
                    <th>Номер полиса</th>
                    <th>Владелец</th>
                    <th>Автомобиль</th>
                    <th>Тариф</th>
                    <th>Период</th>
                    <th>Цена</th>
                    <th>Статус</th>
                    <th>Действия</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty policies}">
                        <tr><td colspan="8" class="muted">Полисы не найдены.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="policyInfo" items="${policies}">
                            <tr>
                                <td><strong>${policyInfo.policy.policyNumber}</strong></td>
                                <td>${policyInfo.owner.name} ${policyInfo.owner.surname}</td>
                                <td>${policyInfo.vehicle.brand} ${policyInfo.vehicle.model}<br>
                                    <span class="muted" style="font-size:12px;">${policyInfo.vehicle.reg}</span>
                                </td>
                                <td>${policyInfo.tariff.policyType}</td>
                                <td>${policyInfo.policy.startDate} - ${policyInfo.policy.endDate}</td>
                                <td><strong style="color:var(--success)">${policyInfo.policy.price} ₽</strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${policyInfo.policy.statId == 1}">
                                            <span style="color:var(--success); font-weight:bold;">Активный</span>
                                        </c:when>
                                        <c:when test="${policyInfo.policy.statId == 2}">
                                            <span style="color:var(--muted); font-weight:bold;">Неактивный</span>
                                        </c:when>
                                        <c:when test="${policyInfo.policy.statId == 3}">
                                            <span style="color:var(--warning); font-weight:bold;">В обработке</span>
                                        </c:when>
                                        <c:when test="${policyInfo.policy.statId == 5}">
                                            <span style="color:var(--error); font-weight:bold;">Отклонен</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="muted">Неизвестно</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div style="display:flex; gap:8px;">
                                        <c:if test="${policyInfo.policy.statId == 1 && policyInfo.policy.active}">
                                            <form method="post" style="display:inline;" class="table-inline-form extend-form" data-policy-number="${policyInfo.policy.policyNumber}">
                                                <input type="hidden" name="action" value="extend"/>
                                                <input type="hidden" name="policyId" value="${policyInfo.policy.id}"/>
                                                <button class="btn btn-primary" type="button" style="font-size:12px; padding:4px 8px;">Продлить</button>
                                            </form>
                                            <form method="post" style="display:inline;" class="table-inline-form cancel-form" data-policy-number="${policyInfo.policy.policyNumber}">
                                                <input type="hidden" name="action" value="cancel"/>
                                                <input type="hidden" name="policyId" value="${policyInfo.policy.id}"/>
                                                <button class="btn btn-ghost" type="button" style="font-size:12px; padding:4px 8px;">Аннулировать</button>
                                            </form>
                                        </c:if>
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
// Подтверждение действий с полисами
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
    
    // Подтверждение продления
    const extendForms = document.querySelectorAll('form.extend-form');
    extendForms.forEach(form => {
        const submitBtn = form.querySelector('button[type="button"]');
        if (submitBtn) {
            submitBtn.addEventListener('click', function(e) {
                e.preventDefault();
                const policyNumber = form.getAttribute('data-policy-number') || 'этот полис';
                formToSubmit = form;
                showConfirmModal(
                    'Подтверждение продления',
                    'Вы уверены, что хотите продлить полис ' + policyNumber + ' на 1 год?',
                    () => {
                        if (formToSubmit) {
                            formToSubmit.submit();
                        }
                    }
                );
            });
        }
    });
    
    // Подтверждение аннулирования
    const cancelForms = document.querySelectorAll('form.cancel-form');
    cancelForms.forEach(form => {
        const submitBtn = form.querySelector('button[type="button"]');
        if (submitBtn) {
            submitBtn.addEventListener('click', function(e) {
                e.preventDefault();
                const policyNumber = form.getAttribute('data-policy-number') || 'этот полис';
                formToSubmit = form;
                showConfirmModal(
                    'Подтверждение аннулирования',
                    'Вы уверены, что хотите аннулировать полис ' + policyNumber + '?',
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

