package com.example.insurancecompany.servlet.AdminTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.InsuranceCase;
import com.example.insurancecompany.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

public class AdminInsuranceServlet extends HttpServlet {

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        this.adminService = (AdminService) getServletContext().getAttribute("adminService");
        if (this.adminService == null) {
            throw new IllegalStateException("AdminService not initialized");
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
        if (!"ADMIN".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/user/home");
            return;
        }

        try {
            String statusFilter = req.getParameter("status");
            List<InsuranceCase> cases;
            if (statusFilter != null && !statusFilter.isEmpty()) {
                cases = adminService.getInsuranceCasesByStatus(Integer.parseInt(statusFilter));
            } else {
                cases = adminService.getAllInsuranceCases();
            }
            
            // Добавляем фотографии к каждому случаю
            com.example.insurancecompany.dao.InsurancePhotoDao insurancePhotoDao = 
                (com.example.insurancecompany.dao.InsurancePhotoDao) getServletContext().getAttribute("insurancePhotoDao");
            
            java.util.List<java.util.Map<String, Object>> casesWithPhotos = new java.util.ArrayList<>();
            for (InsuranceCase insuranceCase : cases) {
                java.util.Map<String, Object> caseInfo = new java.util.HashMap<>();
                caseInfo.put("insuranceCase", insuranceCase);
                if (insurancePhotoDao != null) {
                    try {
                        java.util.List<com.example.insurancecompany.model.InsurancePhoto> photos = 
                            insurancePhotoDao.findByInsuranceId(insuranceCase.getId());
                        caseInfo.put("photos", photos);
                    } catch (Exception e) {
                        caseInfo.put("photos", new java.util.ArrayList<>());
                    }
                } else {
                    caseInfo.put("photos", new java.util.ArrayList<>());
                }
                casesWithPhotos.add(caseInfo);
            }
            
            req.setAttribute("cases", casesWithPhotos);
            
            // Получаем статусы для отображения
            com.example.insurancecompany.dao.StatusDao statusDao = 
                (com.example.insurancecompany.dao.StatusDao) getServletContext().getAttribute("statusDao");
            if (statusDao != null) {
                req.setAttribute("statuses", statusDao.findAll());
            }
            
            req.getRequestDispatcher("/jsp/admin_insurances.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при получении списка страховых случаев: " + 
                (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            try {
                req.getRequestDispatcher("/jsp/admin_insurances.jsp").forward(req, resp);
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
        if (!"ADMIN".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/user/home");
            return;
        }

        String action = req.getParameter("action");
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            String comment = req.getParameter("comment");

            if ("approve".equals(action)) {
                adminService.approveInsuranceCase(id, comment);
                
                // Логирование
                try {
                    com.example.insurancecompany.service.ActionLogService actionLogService = 
                        (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                    if (actionLogService != null) {
                        String ipAddress = req.getRemoteAddr();
                        actionLogService.logAction(user.getId(), user.getRole(), "APPROVE_INSURANCE", "InsuranceCase", id, 
                            "Администратор одобрил страховой случай ID: " + id + (comment != null && !comment.isEmpty() ? ". Комментарий: " + comment : ""), ipAddress);
                    }
                } catch (Exception e) {}
                
            } else if ("reject".equals(action)) {
                adminService.rejectInsuranceCase(id, comment);
                
                // Логирование
                try {
                    com.example.insurancecompany.service.ActionLogService actionLogService = 
                        (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                    if (actionLogService != null) {
                        String ipAddress = req.getRemoteAddr();
                        actionLogService.logAction(user.getId(), user.getRole(), "REJECT_INSURANCE", "InsuranceCase", id, 
                            "Администратор отклонил страховой случай ID: " + id + (comment != null && !comment.isEmpty() ? ". Комментарий: " + comment : ""), ipAddress);
                    }
                } catch (Exception e) {}
            }
            resp.sendRedirect(req.getContextPath() + "/admin/insurances");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
