<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Управление выплатами</title>
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
        <h3>Создать выплату</h3>
                <form method="post" action="${pageContext.request.contextPath}/admin/payouts">
                    <div class="form-group">
                        <label>Страховой случай (одобренный)</label>
                        <select name="insuranceId" id="insuranceSelect" required>
                            <option value="">Выберите страховой случай</option>
                            <c:forEach var="caseInfo" items="${approvedCases}">
                                <c:if test="${caseInfo.policy != null && caseInfo.vehicle != null && caseInfo.tariff != null && caseInfo.owner != null}">
                                <option value="${caseInfo.insuranceCase.id}"
                                    data-policy-number="<c:out value='${caseInfo.policy.policyNumber}'/>"
                                    data-owner-name="<c:out value='${caseInfo.owner.name}'/> <c:out value='${caseInfo.owner.surname}'/>"
                                    data-vehicle="<c:out value='${caseInfo.vehicle.brand}'/> <c:out value='${caseInfo.vehicle.model}'/>"
                                    data-reg="<c:out value='${caseInfo.vehicle.reg}'/>"
                                    data-policy-type="<c:out value='${caseInfo.tariff.policyType}'/>"
                                    data-tariff-name="<c:out value='${caseInfo.tariff.tariffName}'/>"
                                    data-incident-date="<c:out value='${caseInfo.insuranceCase.incidentDate}'/>"
                                    data-incident-description="<c:out value='${caseInfo.insuranceCase.incidentDescription}'/>"
                                    data-description-damage="<c:out value='${caseInfo.insuranceCase.descriptionDamage}'/>"
                                    data-grade-damage="<c:out value='${caseInfo.insuranceCase.gradeDamage}'/>"
                                    data-admin-comment="<c:out value='${caseInfo.insuranceCase.adminComment}'/>">
                                    ID: ${caseInfo.insuranceCase.id} | 
                                    Полис: <c:out value="${caseInfo.policy.policyNumber}"/> | 
                                    <c:out value="${caseInfo.vehicle.brand}"/> <c:out value="${caseInfo.vehicle.model}"/> (<c:out value="${caseInfo.vehicle.reg}"/>) | 
                                    <c:out value="${caseInfo.tariff.policyType}"/> | 
                                    Ущерб: <c:out value="${caseInfo.insuranceCase.gradeDamage}"/> ₽
                                </option>
                                </c:if>
                            </c:forEach>
                        </select>
                        <c:if test="${empty approvedCases}">
                            <div class="muted" style="margin-top:8px">Нет одобренных страховых случаев</div>
                        </c:if>
                        <div id="insuranceDetails" class="card" style="display:none; margin-top:12px;">
                            <p class="kicker">Подробная информация о страховом случае</p>
                            <div id="insuranceDetailsContent"></div>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Сумма выплаты</label>
                            <input type="number" name="sumPayout" step="0.01" min="0" required placeholder="0.00"/>
                        </div>
                        <div class="form-group">
                            <label>Способ оплаты</label>
                            <select name="paymentMethod" required>
                                <option value="Наличные">Наличные</option>
                                <option value="На карту">На карту</option>
                            </select>
                        </div>
                    </div>

                    <button class="btn btn-primary" type="button" id="createPayoutBtn">Создать выплату</button>
                </form>
    </div>

    <div class="card">
        <h3>Список выплат</h3>

                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr><th>ID</th><th>ID случая</th><th>Сумма</th><th>Способ оплаты</th><th>Дата</th></tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty payouts}">
                                <tr><td colspan="5" class="muted">Нет выплат.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="payout" items="${payouts}">
                                    <tr>
                                        <td>${payout.id}</td>
                                        <td>${payout.insuranceId}</td>
                                        <td><c:out value="${payout.sumPayout}" default="0"/> ₽</td>
                                        <td><c:out value="${payout.paymentMethod}" default="–"/></td>
                                        <td>
                                            <c:if test="${payout.payoutDate != null}">
                                                <c:out value="${payout.payoutDate}"/>
                                            </c:if>
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
    (function(){
        const insuranceSelect = document.getElementById('insuranceSelect');
        const insuranceDetails = document.getElementById('insuranceDetails');
        const insuranceDetailsContent = document.getElementById('insuranceDetailsContent');
        
        function escapeHtml(text) {
            if (!text) return '';
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
        
        function updateInsuranceDetails() {
            if (!insuranceSelect || !insuranceSelect.value) {
                insuranceDetails.style.display = 'none';
                return;
            }
            
            const selectedOption = insuranceSelect.options[insuranceSelect.selectedIndex];
            if (!selectedOption || selectedOption.value === '') {
                insuranceDetails.style.display = 'none';
                return;
            }
            
            const policyNumber = selectedOption.getAttribute('data-policy-number') || '';
            const ownerName = selectedOption.getAttribute('data-owner-name') || '';
            const vehicle = selectedOption.getAttribute('data-vehicle') || '';
            const reg = selectedOption.getAttribute('data-reg') || '';
            const policyType = selectedOption.getAttribute('data-policy-type') || '';
            const tariffName = selectedOption.getAttribute('data-tariff-name') || '';
            const incidentDate = selectedOption.getAttribute('data-incident-date') || '';
            const incidentDescription = selectedOption.getAttribute('data-incident-description') || '';
            const descriptionDamage = selectedOption.getAttribute('data-description-damage') || '';
            const gradeDamage = selectedOption.getAttribute('data-grade-damage') || '';
            const adminComment = selectedOption.getAttribute('data-admin-comment') || '';
            
            insuranceDetailsContent.innerHTML = 
                '<div style="line-height:1.8;">' +
                '<p><strong>Владелец:</strong> ' + escapeHtml(ownerName) + '</p>' +
                '<p><strong>Полис:</strong> ' + escapeHtml(policyNumber) + '</p>' +
                '<p><strong>Автомобиль:</strong> ' + escapeHtml(vehicle) + '</p>' +
                '<p><strong>Гос. номер:</strong> ' + escapeHtml(reg) + '</p>' +
                '<p><strong>Тип полиса:</strong> ' + escapeHtml(policyType) + (tariffName ? ' (' + escapeHtml(tariffName) + ')' : '') + '</p>' +
                '<p><strong>Дата происшествия:</strong> ' + escapeHtml(incidentDate) + '</p>' +
                '<p><strong>Описание происшествия:</strong> ' + escapeHtml(incidentDescription || '–') + '</p>' +
                '<p><strong>Описание повреждений:</strong> ' + escapeHtml(descriptionDamage || '–') + '</p>' +
                '<p style="font-size:18px; font-weight:bold; color:var(--error); margin-top:12px;">' +
                '<strong>Оценочный ущерб:</strong> ' + escapeHtml(gradeDamage) + ' ₽</p>' +
                (adminComment ? '<p><strong>Комментарий администратора:</strong> ' + escapeHtml(adminComment) + '</p>' : '') +
                '</div>';
            insuranceDetails.style.display = 'block';
        }
        
        if(insuranceSelect) {
            insuranceSelect.addEventListener('change', updateInsuranceDetails);
        }
    })();
    
    // Подтверждение создания выплаты
    document.addEventListener('DOMContentLoaded', function() {
        const confirmModal = document.getElementById('confirmModal');
        const confirmModalTitle = document.getElementById('confirmModalTitle');
        const confirmModalMessage = document.getElementById('confirmModalMessage');
        const confirmModalCancel = document.getElementById('confirmModalCancel');
        const confirmModalConfirm = document.getElementById('confirmModalConfirm');
        const payoutForm = document.querySelector('form[action*="payouts"]');
        const createPayoutBtn = document.getElementById('createPayoutBtn');
        
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
        
        if (createPayoutBtn && payoutForm) {
            createPayoutBtn.addEventListener('click', function(e) {
                e.preventDefault();
                const insuranceId = document.getElementById('insuranceSelect').value;
                const sumPayout = document.querySelector('input[name="sumPayout"]').value;
                if (!insuranceId || !sumPayout) {
                    payoutForm.reportValidity();
                    return;
                }
                showConfirmModal(
                    'Подтверждение создания выплаты',
                    'Вы уверены, что хотите создать выплату на сумму ' + sumPayout + ' ₽?',
                    () => {
                        payoutForm.submit();
                    }
                );
            });
        }
    });
</script>
</body>
</html>
