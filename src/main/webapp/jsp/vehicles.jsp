<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Мои автомобили</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.2">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="app">
    <div style="margin-bottom: 16px;">
        <a href="${pageContext.request.contextPath}/user/home" class="btn btn-ghost">← На главную</a>
    </div>
    <div class="card">
        <h3 id="formTitle">Добавить автомобиль</h3>
                <form method="post" action="${pageContext.request.contextPath}/user/vehicles" id="vehicleForm">
                    <input type="hidden" name="action" value="create" id="formAction"/>
                    <input type="hidden" name="id" id="vehicleId"/>
                    <div class="form-row">
                        <div class="form-group">
                            <label>VIN</label>
                            <input name="vin" required type="text"/>
                        </div>
                        <div class="form-group">
                            <label>Гос. номер</label>
                            <input name="reg" required type="text"/>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Марка</label>
                            <input name="brand" required type="text"/>
                        </div>
                        <div class="form-group">
                            <label>Модель</label>
                            <input name="model" required type="text"/>
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Год выпуска</label>
                            <input name="yearManufact" required type="number"/>
                        </div>
                        <div class="form-group">
                            <label>Мощность (л.с.)</label>
                            <input name="horsePower" required type="number"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Категория</label>
                        <input name="categoryLic" required type="text"/>
                    </div>

                    <button class="btn btn-primary" type="submit" id="submitBtn">Добавить</button>
                    <button type="button" onclick="cancelEdit()" class="btn btn-ghost" id="cancelBtn" style="display:none;">Отмена</button>
                </form>
    </div>

    <div class="card">
        <h3>Список автомобилей</h3>
                <div class="table-wrap">
                    <table>
                        <thead><tr><th>VIN</th><th>Гос. номер</th><th>Марка</th><th>Модель</th><th>Год</th><th>Мощность</th><th>Действия</th></tr></thead>
                        <tbody>
                        <c:forEach var="vehicle" items="${vehicles}">
                            <tr>
                                <td>${vehicle.vin}</td>
                                <td>${vehicle.reg}</td>
                                <td>${vehicle.brand}</td>
                                <td>${vehicle.model}</td>
                                <td>${vehicle.yearManufact}</td>
                                <td>${vehicle.horsePower}</td>
                                <td>
                                    <div style="display:flex; gap:8px;">
                                        <button onclick="editVehicle(${vehicle.id}, '${vehicle.vin}', '${vehicle.reg}', '${vehicle.brand}', '${vehicle.model}', ${vehicle.yearManufact}, ${vehicle.horsePower}, '${vehicle.categoryLic}')" class="btn btn-ghost" style="font-size:12px; padding:4px 8px;">Редактировать</button>
                                        <form method="post" style="display:inline;" class="table-inline-form delete-form" data-vehicle-info="${vehicle.brand} ${vehicle.model} (${vehicle.reg})">
                                            <input type="hidden" name="action" value="delete"/>
                                            <input type="hidden" name="id" value="${vehicle.id}"/>
                                            <button class="btn btn-ghost" type="button" style="font-size:12px; padding:4px 8px;">Удалить</button>
                                        </form>
                                    </div>
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
function editVehicle(id, vin, reg, brand, model, yearManufact, horsePower, categoryLic) {
    document.getElementById('formTitle').textContent = 'Редактировать автомобиль';
    document.getElementById('formAction').value = 'update';
    document.getElementById('vehicleId').value = id;
    document.querySelector('input[name="vin"]').value = vin;
    document.querySelector('input[name="reg"]').value = reg;
    document.querySelector('input[name="brand"]').value = brand;
    document.querySelector('input[name="model"]').value = model;
    document.querySelector('input[name="yearManufact"]').value = yearManufact;
    document.querySelector('input[name="horsePower"]').value = horsePower;
    document.querySelector('input[name="categoryLic"]').value = categoryLic;
    document.getElementById('submitBtn').textContent = 'Сохранить';
    document.getElementById('cancelBtn').style.display = 'inline-block';
    document.getElementById('vehicleForm').scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function cancelEdit() {
    document.getElementById('formTitle').textContent = 'Добавить автомобиль';
    document.getElementById('formAction').value = 'create';
    document.getElementById('vehicleId').value = '';
    document.getElementById('vehicleForm').reset();
    document.getElementById('submitBtn').textContent = 'Добавить';
    document.getElementById('cancelBtn').style.display = 'none';
}

// Подтверждение удаления автомобиля
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
        
        // Закрытие при клике на фон
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
                const vehicleInfo = form.getAttribute('data-vehicle-info') || 'этот автомобиль';
                formToSubmit = form;
                showConfirmModal(
                    'Подтверждение удаления',
                    'Вы уверены, что хотите удалить ' + vehicleInfo + '?',
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
