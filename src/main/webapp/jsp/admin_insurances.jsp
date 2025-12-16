<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Управление страховыми случаями</title>
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
        <h3>Управление страховыми случаями</h3>

                <c:if test="${not empty error}">
                    <div class="alert alert-error">${error}</div>
                </c:if>

                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr>
                            <th>ID</th><th>Дата</th><th>Описание</th><th>Повреждения</th><th>Ущерб</th><th>Фото</th><th>Статус</th><th>Действия</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty cases}">
                                <tr><td colspan="8" class="muted">Нет страховых случаев.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="caseInfo" items="${cases}">
                                    <tr>
                                        <td>${caseInfo.insuranceCase.id}</td>
                                        <td>
                                            <c:if test="${caseInfo.insuranceCase.incidentDate != null}">
                                                <c:out value="${caseInfo.insuranceCase.incidentDate}"/>
                                            </c:if>
                                        </td>
                                        <td><c:out value="${caseInfo.insuranceCase.incidentDescription}" default="–"/></td>
                                        <td><c:out value="${caseInfo.insuranceCase.descriptionDamage}" default="–"/></td>
                                        <td><c:out value="${caseInfo.insuranceCase.gradeDamage}" default="0"/> ₽</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty caseInfo.photos}">
                                                    <div style="display:flex; gap:4px; flex-wrap:wrap;">
                                                        <c:forEach var="photo" items="${caseInfo.photos}">
                                                            <a href="${pageContext.request.contextPath}/photo/${photo.id}" target="_blank" 
                                                               style="display:inline-block; width:50px; height:50px; border:1px solid var(--color-border); border-radius:4px; overflow:hidden; background:var(--color-bg-secondary);"
                                                               title="${photo.fileName}">
                                                                <c:choose>
                                                                    <c:when test="${photo.mimeType != null && photo.mimeType.startsWith('image/')}">
                                                                        <img src="${pageContext.request.contextPath}/photo/${photo.id}" 
                                                                             alt="${photo.fileName}" 
                                                                             style="width:100%; height:100%; object-fit:cover;"/>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <div style="width:100%; height:100%; display:flex; align-items:center; justify-content:center; color:var(--color-text-secondary);">
                                                                            📄
                                                                        </div>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </a>
                                                        </c:forEach>
                                                    </div>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="muted">Нет фото</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:forEach var="status" items="${statuses}">
                                                <c:if test="${status.id == caseInfo.insuranceCase.statId}">
                            <span class="badge
                              <c:choose>
                                <c:when test="${status.id == 3}"> badge-pending</c:when>
                                <c:when test="${status.id == 4}"> badge-approved</c:when>
                                <c:when test="${status.id == 5}"> badge-rejected</c:when>
                                <c:when test="${status.id == 8}"> badge-paid</c:when>
                                <c:otherwise> badge-default</c:otherwise>
                              </c:choose>
                            ">${status.stName}</span>
                                                </c:if>
                                            </c:forEach>
                                        </td>
                                        <td>
                                            <c:if test="${caseInfo.insuranceCase.statId == 3}">
                                                <form method="post" class="table-inline-form approve-form" data-case-id="${caseInfo.insuranceCase.id}">
                                                    <input type="hidden" name="action" value="approve"/>
                                                    <input type="hidden" name="id" value="${caseInfo.insuranceCase.id}"/>
                                                    <input type="text" name="comment" placeholder="Комментарий" class="comment-input" />
                                                    <button class="btn btn-primary" type="button">Одобрить</button>
                                                </form>

                                                <form method="post" class="table-inline-form reject-form" data-case-id="${caseInfo.insuranceCase.id}">
                                                    <input type="hidden" name="action" value="reject"/>
                                                    <input type="hidden" name="id" value="${caseInfo.insuranceCase.id}"/>
                                                    <input type="text" name="comment" placeholder="Комментарий" class="comment-input" />
                                                    <button class="btn btn-danger" type="button">Отклонить</button>
                                                </form>
                                            </c:if>

                                            <c:if test="${caseInfo.insuranceCase.statId != 3}">
                                                <span class="muted">Нет доступных действий</span>
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
// Подтверждение действий со страховыми случаями
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
                const caseId = form.getAttribute('data-case-id');
                formToSubmit = form;
                showConfirmModal(
                    'Подтверждение одобрения',
                    'Вы уверены, что хотите одобрить страховой случай #' + caseId + '?',
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
                const caseId = form.getAttribute('data-case-id');
                formToSubmit = form;
                showConfirmModal(
                    'Подтверждение отклонения',
                    'Вы уверены, что хотите отклонить страховой случай #' + caseId + '?',
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
