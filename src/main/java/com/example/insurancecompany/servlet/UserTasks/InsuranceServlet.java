package com.example.insurancecompany.servlet.UserTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.InsuranceCase;
import com.example.insurancecompany.model.InsurancePhoto;
import com.example.insurancecompany.model.Policy;
import com.example.insurancecompany.model.Vehicle;
import com.example.insurancecompany.model.Tariff;
import com.example.insurancecompany.service.InsuranceService;
import com.example.insurancecompany.service.PolicyService;
import com.example.insurancecompany.dao.VehicleDao;
import com.example.insurancecompany.dao.TariffDao;
import com.example.insurancecompany.dao.InsurancePhotoDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.MultipartConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@MultipartConfig(maxFileSize = 10485760, maxRequestSize = 52428800) // 10MB per file, 50MB total
public class InsuranceServlet extends HttpServlet {

    private InsuranceService insuranceService;
    private PolicyService policyService;
    private VehicleDao vehicleDao;
    private TariffDao tariffDao;
    private InsurancePhotoDao insurancePhotoDao;
    private static final String UPLOAD_DIR = "uploads/insurance_photos";

    @Override
    public void init() throws ServletException {
        this.insuranceService = (InsuranceService) getServletContext().getAttribute("insuranceService");
        this.policyService = (PolicyService) getServletContext().getAttribute("policyService");
        this.vehicleDao = (VehicleDao) getServletContext().getAttribute("vehicleDao");
        this.tariffDao = (TariffDao) getServletContext().getAttribute("tariffDao");
        this.insurancePhotoDao = (InsurancePhotoDao) getServletContext().getAttribute("insurancePhotoDao");
        if (this.insuranceService == null || this.policyService == null || this.vehicleDao == null || this.tariffDao == null) {
            throw new IllegalStateException("Services not initialized");
        }
        
        // Создаем директорию для загрузки файлов
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserAccount user = (UserAccount) session.getAttribute("currentUser");
        try {
            if (user.getOwnerId() != null) {
                List<InsuranceCase> cases = insuranceService.getInsuranceCasesByOwnerId(user.getOwnerId());
                
                // Добавляем фотографии к каждому случаю
                List<Map<String, Object>> casesWithPhotos = new ArrayList<>();
                for (InsuranceCase insuranceCase : cases) {
                    Map<String, Object> caseInfo = new HashMap<>();
                    caseInfo.put("insuranceCase", insuranceCase);
                    if (insurancePhotoDao != null) {
                        try {
                            List<InsurancePhoto> photos = insurancePhotoDao.findByInsuranceId(insuranceCase.getId());
                            caseInfo.put("photos", photos);
                        } catch (Exception e) {
                            caseInfo.put("photos", new ArrayList<>());
                        }
                    } else {
                        caseInfo.put("photos", new ArrayList<>());
                    }
                    casesWithPhotos.add(caseInfo);
                }
                
                req.setAttribute("cases", casesWithPhotos);
                
                // Для формы создания заявки - только активные полисы (статус = 1)
                List<Policy> activePolicies = policyService.getActivePoliciesByOwnerId(user.getOwnerId());
                
                // Создаем список с дополнительной информацией о полисах
                List<Map<String, Object>> policiesWithDetails = new ArrayList<>();
                for (Policy policy : activePolicies) {
                    Map<String, Object> policyInfo = new HashMap<>();
                    policyInfo.put("policy", policy);
                    
                    Vehicle vehicle = vehicleDao.findById(policy.getVehicleId());
                    Tariff tariff = tariffDao.findById(policy.getTariffId());
                    
                    policyInfo.put("vehicle", vehicle);
                    policyInfo.put("tariff", tariff);
                    
                    policiesWithDetails.add(policyInfo);
                }
                
                req.setAttribute("policies", policiesWithDetails);
                
                // Получаем статусы для отображения
                com.example.insurancecompany.dao.StatusDao statusDao = 
                    (com.example.insurancecompany.dao.StatusDao) getServletContext().getAttribute("statusDao");
                if (statusDao != null) {
                    req.setAttribute("statuses", statusDao.findAll());
                }
            }
            req.getRequestDispatcher("/jsp/insurances.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при получении списка страховых случаев: " + 
                (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            try {
                req.getRequestDispatcher("/jsp/insurances.jsp").forward(req, resp);
            } catch (Exception ex) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        UserAccount user = (UserAccount) session.getAttribute("currentUser");
        if (user.getOwnerId() == null) {
            resp.sendRedirect(req.getContextPath() + "/user/home");
            return;
        }

        try {
            InsuranceCase insuranceCase = new InsuranceCase();
            insuranceCase.setPolicyId(Integer.parseInt(req.getParameter("policyId")));
            insuranceCase.setOwnerId(user.getOwnerId());
            
            // Парсинг datetime-local формата (YYYY-MM-DDTHH:mm)
            String incidentDateStr = req.getParameter("incidentDate");
            if (incidentDateStr != null && !incidentDateStr.isEmpty()) {
                incidentDateStr = incidentDateStr.replace("T", " ") + ":00";
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                insuranceCase.setIncidentDate(LocalDateTime.parse(incidentDateStr, formatter));
            } else {
                insuranceCase.setIncidentDate(LocalDateTime.now());
            }
            
            insuranceCase.setIncidentDescription(req.getParameter("incidentDescription"));
            insuranceCase.setDescriptionDamage(req.getParameter("descriptionDamage"));
            insuranceCase.setGradeDamage(new BigDecimal(req.getParameter("gradeDamage")));
            
            insuranceService.createInsuranceCase(insuranceCase);
            
            // Обработка загрузки фотографий
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            List<Part> fileParts = req.getParts().stream()
                .filter(part -> "photos".equals(part.getName()) && part.getSize() > 0)
                .collect(java.util.stream.Collectors.toList());
            
            for (Part filePart : fileParts) {
                String fileName = filePart.getSubmittedFileName();
                if (fileName != null && !fileName.isEmpty()) {
                    // Генерируем уникальное имя файла
                    String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                    String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
                    String filePath = uploadDir.getAbsolutePath() + File.separator + uniqueFileName;
                    
                    // Сохраняем файл
                    try (InputStream inputStream = filePart.getInputStream()) {
                        Files.copy(inputStream, new File(filePath).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    
                    // Сохраняем информацию о файле в БД
                    InsurancePhoto photo = new InsurancePhoto();
                    photo.setInsuranceId(insuranceCase.getId());
                    photo.setFileName(fileName);
                    photo.setFilePath(UPLOAD_DIR + "/" + uniqueFileName);
                    photo.setFileSize(filePart.getSize());
                    photo.setMimeType(filePart.getContentType());
                    photo.setCreatedAt(LocalDateTime.now());
                    
                    if (insurancePhotoDao != null) {
                        insurancePhotoDao.create(photo);
                    }
                }
            }
            
            // Логирование
            try {
                com.example.insurancecompany.service.ActionLogService actionLogService = 
                    (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                if (actionLogService != null) {
                    String ipAddress = req.getRemoteAddr();
                    actionLogService.logAction(user.getId(), user.getRole(), "CREATE", "InsuranceCase", insuranceCase.getId(), 
                        "Создан страховой случай с " + fileParts.size() + " фотографиями", ipAddress);
                }
            } catch (Exception e) {}
            
            resp.sendRedirect(req.getContextPath() + "/user/insurances");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при создании заявки: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
