<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мои полисы</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.1">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="app">
    <div style="margin-bottom: 16px;">
        <a href="${pageContext.request.contextPath}/user/home" class="btn btn-ghost">← На главную</a>
    </div>

    <c:if test="${not empty sessionScope.successMessage}">
        <div class="card" style="background-color: var(--success-light); border-color: var(--success); margin-bottom: 16px;">
            <p style="color: var(--success); margin: 0;">${sessionScope.successMessage}</p>
        </div>
        <c:remove var="successMessage" scope="session"/>
    </c:if>
    <div class="card">
                <h3>Оформить полис</h3>

                <form method="post" action="${pageContext.request.contextPath}/user/policies">
                    <div class="form-group">
                        <label>Автомобиль</label>
                        <select name="vehicleId" required>
                            <option value="">Выберите автомобиль</option>
                            <c:forEach var="vehicle" items="${vehicles}">
                                <option value="${vehicle.id}">${vehicle.brand} ${vehicle.model} (${vehicle.reg})</option>
                            </c:forEach>
                        </select>
                    </div>

                    <div class="form-group">
                        <label>Тариф</label>
                        <select name="tariffId" id="tariffSelect" required>
                            <option value="">Выберите тариф</option>
                            <c:forEach var="tariff" items="${tariffs}">
                                <option value="${tariff.id}" 
                                        data-base-price="${tariff.basePrice}"
                                        data-policy-type="${tariff.policyType}"
                                        data-description="${tariff.description != null ? tariff.description : ''}">
                                        ${tariff.tariffName} (${tariff.policyType}) - ${tariff.basePrice} ₽/мес
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div id="tariffDescription" class="card" style="display:none; margin-top: 12px;">
                        <p class="kicker" id="tariffTypeName"></p>
                        <p class="muted" id="tariffDescriptionText"></p>
                    </div>

                    <div id="priceInfo" class="card" style="display:none; margin-top: 12px;">
                        <p class="kicker">Расчёт стоимости</p>
                        <p class="muted">Базовая цена указана за месяц. Итог рассчитывается по периоду, стажу и мощности автомобиля.</p>
                        <p id="estimatedPrice" style="font-size: 18px; font-weight: bold; color: var(--success); margin-top: 8px;"></p>
                    </div>

                    <div class="form-row" style="margin-top:12px">
                        <div class="form-group">
                            <label>Дата начала</label>
                            <input type="date" name="startDate" id="startDate" required/>
                        </div>
                        <div class="form-group">
                            <label>Период действия</label>
                            <select name="policyPeriod" id="policyPeriod" required>
                                <option value="1">1 месяц</option>
                                <option value="3">3 месяца</option>
                                <option value="6">6 месяцев</option>
                                <option value="12">1 год</option>
                                <option value="24">2 года</option>
                            </select>
                            <input type="hidden" name="endDate" id="endDate"/>
                        </div>
                    </div>

                    <div style="margin-top:12px">
                        <button class="btn btn-primary" type="button" id="submitBtn">Оформить полис</button>
                    </div>
                </form>
    </div>

    <div class="card">
        <h3>Мои полисы</h3>
                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr><th>Номер полиса</th><th>Дата начала</th><th>Дата окончания</th><th>Цена</th><th>Статус</th></tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty policies}">
                                <tr><td colspan="5" class="muted" style="text-align:center;padding:24px;">У вас пока нет оформленных полисов.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="policy" items="${policies}">
                                    <tr>
                                        <td><strong>${policy.policyNumber}</strong></td>
                                        <td>${policy.startDate}</td>
                                        <td>${policy.endDate}</td>
                                        <td><strong style="color:var(--success)">${policy.price} ₽</strong></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${policy.statId == 1}">
                                                    <span style="color:var(--success); font-weight:bold;">Активный</span>
                                                </c:when>
                                                <c:when test="${policy.statId == 3}">
                                                    <span style="color:var(--warning); font-weight:bold;">В обработке</span>
                                                </c:when>
                                                <c:when test="${policy.statId == 5}">
                                                    <span style="color:var(--error); font-weight:bold;">Отклонен</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="muted">Неизвестно</span>
                                                </c:otherwise>
                                            </c:choose>
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
<div id="confirmModal" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:1000; align-items:center; justify-content:center; flex-direction:column;">
    <div class="card" style="max-width:500px; width:90%; margin:0 auto;">
        <h3>Подтверждение оформления полиса</h3>
        <div id="confirmModalContent" style="margin:16px 0;">
            <!-- Содержимое будет заполнено динамически -->
        </div>
        <div style="display:flex; gap:10px; justify-content:flex-end; margin-top:16px;">
            <button class="btn btn-ghost" id="cancelBtn">Отмена</button>
            <button class="btn btn-primary" id="confirmBtn">Подтвердить</button>
        </div>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>

<script>
    (function(){
        const tariffSelect = document.getElementById('tariffSelect');
        const priceInfo = document.getElementById('priceInfo');
        const tariffDescription = document.getElementById('tariffDescription');
        const tariffTypeName = document.getElementById('tariffTypeName');
        const tariffDescriptionText = document.getElementById('tariffDescriptionText');
        const estimatedPrice = document.getElementById('estimatedPrice');
        const startDateInput = document.getElementById('startDate');
        const periodSelect = document.getElementById('policyPeriod');
        const endDateInput = document.getElementById('endDate');
        const vehicleSelect = document.querySelector('select[name="vehicleId"]');
        const form = document.querySelector('form');
        const submitBtn = document.getElementById('submitBtn');

        // Описания тарифов по умолчанию
        const defaultDescriptions = {
            'ОСАГО': 'Обязательное страхование автогражданской ответственности. Покрывает ущерб, причиненный третьим лицам в результате ДТП.',
            'КАСКО': 'Добровольное страхование автомобиля от ущерба, хищения и угона. Защищает ваш автомобиль в любых ситуациях.',
            'Зелёная карта': 'Международное страхование для поездок за границу. Обязательно для выезда на автомобиле в страны, подписавшие соглашение о Зелёной карте.',
            'Страховка водителя и пассажиров': 'Страхование жизни и здоровья водителя и пассажиров при ДТП. Покрывает медицинские расходы и выплаты при травмах.'
        };

        function updateTariffDescription() {
            if (!tariffSelect || !tariffSelect.value) {
                tariffDescription.style.display = 'none';
                return;
            }

            const selectedOption = tariffSelect.options[tariffSelect.selectedIndex];
            const policyType = selectedOption.getAttribute('data-policy-type');
            const description = selectedOption.getAttribute('data-description') || defaultDescriptions[policyType] || '';

            if (description) {
                tariffTypeName.textContent = policyType;
                tariffDescriptionText.textContent = description;
                tariffDescription.style.display = 'block';
            } else {
                tariffDescription.style.display = 'none';
            }
        }

        function calculateEndDate(){
            if(!startDateInput || !periodSelect || !endDateInput) return;
            if(startDateInput.value && periodSelect.value){
                const start = new Date(startDateInput.value);
                const months = parseInt(periodSelect.value,10);
                const end = new Date(start);
                end.setMonth(end.getMonth() + months);
                end.setDate(end.getDate()-1);
                const y = end.getFullYear(), m = String(end.getMonth()+1).padStart(2,'0'), d = String(end.getDate()).padStart(2,'0');
                endDateInput.value = `${y}-${m}-${d}`;
                calculatePrice();
            }
        }

        function calculatePrice() {
            if (!vehicleSelect || !vehicleSelect.value || !tariffSelect || !tariffSelect.value || 
                !startDateInput || !startDateInput.value || !periodSelect || !periodSelect.value) {
                priceInfo.style.display = 'none';
                return;
            }

            const vehicleId = vehicleSelect.value;
            const tariffId = tariffSelect.value;
            const months = parseInt(periodSelect.value, 10);

            // AJAX запрос для расчета цены
            const xhr = new XMLHttpRequest();
            xhr.open('POST', '${pageContext.request.contextPath}/user/policies', true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onreadystatechange = function() {
                if (xhr.readyState === 4) {
                    if (xhr.status === 200) {
                        try {
                            const response = JSON.parse(xhr.responseText);
                            if (response.price) {
                                estimatedPrice.textContent = 'Итоговая стоимость: ' + response.price + ' ₽';
                                priceInfo.style.display = 'block';
                            }
                        } catch (e) {
                            console.error('Ошибка парсинга ответа:', e);
                        }
                    }
                }
            };
            xhr.send('action=calculatePrice&vehicleId=' + vehicleId + '&tariffId=' + tariffId + '&months=' + months);
        }

        if(tariffSelect){
            tariffSelect.addEventListener('change', function(){
                updateTariffDescription();
                calculatePrice();
            });
        }

        if(vehicleSelect){
            vehicleSelect.addEventListener('change', calculatePrice);
        }

        if(startDateInput) {
            startDateInput.addEventListener('change', function(){
                calculateEndDate();
            });
        }
        if(periodSelect) {
            periodSelect.addEventListener('change', function(){
                calculateEndDate();
            });
        }

        // Модальное окно подтверждения
        const confirmModal = document.getElementById('confirmModal');
        const confirmModalContent = document.getElementById('confirmModalContent');
        const confirmBtn = document.getElementById('confirmBtn');
        const cancelBtn = document.getElementById('cancelBtn');

        function showConfirmModal(vehicleText, tariffText, startDate, months, price) {
            const periodText = months === 1 ? 'месяц' : months < 5 ? 'месяца' : 'месяцев';
            confirmModalContent.innerHTML = 
                '<div style="line-height:1.8;">' +
                '<p><strong>Автомобиль:</strong> ' + vehicleText + '</p>' +
                '<p><strong>Тариф:</strong> ' + tariffText + '</p>' +
                '<p><strong>Дата начала:</strong> ' + startDate + '</p>' +
                '<p><strong>Период действия:</strong> ' + months + ' ' + periodText + '</p>' +
                '<p style="font-size:18px; font-weight:bold; color:var(--success); margin-top:12px;">' +
                '<strong>Итоговая стоимость:</strong> ' + price + ' ₽</p>' +
                '<p class="muted" style="margin-top:12px;">После подтверждения заявка будет отправлена администратору на рассмотрение.</p>' +
                '</div>';
            confirmModal.style.display = 'flex';
        }

        function hideConfirmModal() {
            confirmModal.style.display = 'none';
        }

        if(cancelBtn) {
            cancelBtn.addEventListener('click', hideConfirmModal);
        }

        if(confirmBtn && form) {
            confirmBtn.addEventListener('click', function() {
                hideConfirmModal();
                // Небольшая задержка для плавного закрытия модального окна
                setTimeout(function() {
                    form.submit();
                }, 100);
            });
        }

        // Закрытие модального окна при клике вне его
        if(confirmModal) {
            confirmModal.addEventListener('click', function(e) {
                if (e.target === confirmModal) {
                    hideConfirmModal();
                }
            });
        }

        // Обработка отправки формы с подтверждением
        if(submitBtn && form) {
            submitBtn.addEventListener('click', function(e) {
                e.preventDefault();
                
                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }

                const vehicleId = vehicleSelect.value;
                const tariffId = tariffSelect.value;
                const months = parseInt(periodSelect.value, 10);
                const startDate = startDateInput.value;
                const selectedVehicle = vehicleSelect.options[vehicleSelect.selectedIndex].text;
                const selectedTariff = tariffSelect.options[tariffSelect.selectedIndex].text;

                // Получаем цену для подтверждения
                const xhr = new XMLHttpRequest();
                xhr.open('POST', '${pageContext.request.contextPath}/user/policies', true);
                xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
                xhr.onreadystatechange = function() {
                    if (xhr.readyState === 4 && xhr.status === 200) {
                        try {
                            const response = JSON.parse(xhr.responseText);
                            if (response.price) {
                                showConfirmModal(selectedVehicle, selectedTariff, startDate, months, response.price);
                            } else {
                                alert('Ошибка при расчете стоимости. Попробуйте еще раз.');
                            }
                        } catch (e) {
                            console.error('Ошибка:', e);
                            alert('Ошибка при расчете стоимости. Попробуйте еще раз.');
                        }
                    } else if (xhr.readyState === 4) {
                        alert('Ошибка при расчете стоимости. Попробуйте еще раз.');
                    }
                };
                xhr.send('action=calculatePrice&vehicleId=' + vehicleId + '&tariffId=' + tariffId + '&months=' + months);
            });
        }
    })();
</script>
</body>
</html>
