<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Страховые случаи</title>
    <%@ include file="/jsp/theme-init.jsp" %>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/style.css'/>?v=3.1">
</head>
<body>
<%@ include file="/jsp/header.jsp" %>

<div class="app">
    <div style="margin-bottom: 16px;">
        <a href="${pageContext.request.contextPath}/user/home" class="btn btn-ghost">← На главную</a>
    </div>
    <div class="card">
        <h3>Подать заявку на страховой случай</h3>
                <form method="post" action="${pageContext.request.contextPath}/user/insurances" enctype="multipart/form-data">
                    <div class="form-group">
                        <label>Полис</label>
                        <select name="policyId" required>
                            <option value="">Выберите полис</option>
                            <c:forEach var="policyInfo" items="${policies}">
                                <option value="${policyInfo.policy.id}">
                                    ${policyInfo.policy.policyNumber} - 
                                    ${policyInfo.vehicle.brand} ${policyInfo.vehicle.model} 
                                    (${policyInfo.vehicle.reg}) - 
                                    ${policyInfo.tariff.policyType}
                                </option>
                            </c:forEach>
                        </select>
                        <div class="muted" style="margin-top:8px;font-size:13px">
                            Отображаются только активные полисы
                        </div>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label>Дата происшествия</label>
                            <input type="datetime-local" name="incidentDate" required/>
                        </div>
                        <div class="form-group">
                            <label>Оценочная стоимость ущерба</label>
                            <input type="number" name="gradeDamage" step="0.01" min="0" required placeholder="0.00"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>Описание происшествия</label>
                        <textarea name="incidentDescription" placeholder="Опишите что произошло..." required></textarea>
                    </div>

                    <div class="form-group">
                        <label>Описание повреждений</label>
                        <textarea name="descriptionDamage" placeholder="Опишите повреждения..." required></textarea>
                    </div>

                    <div class="form-group">
                        <label>Фотографии повреждений и документов</label>
                        <input type="file" name="photos" multiple accept="image/*,.pdf" />
                        <div class="muted" style="margin-top:8px;font-size:13px">
                            Можно загрузить несколько файлов. Поддерживаются изображения (JPG, PNG, GIF) и PDF документы. Максимальный размер файла: 10MB.
                        </div>
                    </div>

                    <button class="btn btn-primary" type="submit">Подать заявку</button>
                </form>
    </div>

    <div class="card">
        <h3>Мои заявки</h3>

                <div class="table-wrap">
                    <table>
                        <thead>
                        <tr><th>Дата</th><th>Описание</th><th>Повреждения</th><th>Ущерб</th><th>Фото</th><th>Статус</th></tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty cases}">
                                <tr><td colspan="5" class="muted">У вас пока нет страховых случаев.</td></tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="caseInfo" items="${cases}">
                                    <tr>
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
                                                            <c:set var="photoFileName" value="${photo.filePath.substring(photo.filePath.lastIndexOf('/') + 1)}"/>
                                                            <a href="${pageContext.request.contextPath}/photo/${photo.id}" target="_blank" 
                                                               style="display:inline-block; width:50px; height:50px; border:1px solid var(--border); border-radius:4px; overflow:hidden; background:var(--bg-secondary);"
                                                               title="${photo.fileName}">
                                                                <c:choose>
                                                                    <c:when test="${photo.mimeType != null && photo.mimeType.startsWith('image/')}">
                                                                        <img src="${pageContext.request.contextPath}/photo/${photo.id}" 
                                                                             alt="${photo.fileName}" 
                                                                             style="width:100%; height:100%; object-fit:cover;"/>
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <div style="width:100%; height:100%; display:flex; align-items:center; justify-content:center; color:var(--muted);">
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
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
    </div>
</div>

<%@ include file="/jsp/footer.jsp" %>
</body>
</html>
