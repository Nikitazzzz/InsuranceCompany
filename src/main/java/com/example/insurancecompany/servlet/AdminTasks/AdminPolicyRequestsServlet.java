package com.example.insurancecompany.servlet.AdminTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Policy;
import com.example.insurancecompany.model.Owner;
import com.example.insurancecompany.model.Vehicle;
import com.example.insurancecompany.model.Tariff;
import com.example.insurancecompany.service.AdminService;
import com.example.insurancecompany.dao.OwnerDao;
import com.example.insurancecompany.dao.VehicleDao;
import com.example.insurancecompany.dao.TariffDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPolicyRequestsServlet extends HttpServlet {

    private AdminService adminService;
    private OwnerDao ownerDao;
    private VehicleDao vehicleDao;
    private TariffDao tariffDao;

    @Override
    public void init() throws ServletException {
        this.adminService = (AdminService) getServletContext().getAttribute("adminService");
        this.ownerDao = (OwnerDao) getServletContext().getAttribute("ownerDao");
        this.vehicleDao = (VehicleDao) getServletContext().getAttribute("vehicleDao");
        this.tariffDao = (TariffDao) getServletContext().getAttribute("tariffDao");
        if (this.adminService == null || this.ownerDao == null || this.vehicleDao == null || this.tariffDao == null) {
            throw new IllegalStateException("Services not initialized");
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
            List<Policy> pendingPolicies = adminService.getPendingPolicies();
            
            // Создаем список с дополнительной информацией
            List<Map<String, Object>> policyRequests = new ArrayList<>();
            for (Policy policy : pendingPolicies) {
                Map<String, Object> requestInfo = new HashMap<>();
                requestInfo.put("policy", policy);
                
                Owner owner = ownerDao.findById(policy.getOwnerId());
                Vehicle vehicle = vehicleDao.findById(policy.getVehicleId());
                Tariff tariff = tariffDao.findById(policy.getTariffId());
                
                requestInfo.put("owner", owner);
                requestInfo.put("vehicle", vehicle);
                requestInfo.put("tariff", tariff);
                
                policyRequests.add(requestInfo);
            }
            
            req.setAttribute("policyRequests", policyRequests);
            req.getRequestDispatcher("/jsp/admin_policy_requests.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при получении списка заявок: " + e.getMessage());
            try {
                req.getRequestDispatcher("/jsp/admin_policy_requests.jsp").forward(req, resp);
            } catch (Exception ex) {
                throw new ServletException("Критическая ошибка", ex);
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
            if ("approve".equals(action)) {
                int policyId = Integer.parseInt(req.getParameter("policyId"));
                adminService.approvePolicy(policyId);
                
                // Логирование
                try {
                    com.example.insurancecompany.service.ActionLogService actionLogService = 
                        (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                    if (actionLogService != null) {
                        String ipAddress = req.getRemoteAddr();
                        actionLogService.logAction(user.getId(), user.getRole(), "APPROVE_POLICY", "Policy", policyId, 
                            "Администратор одобрил заявку на полис ID: " + policyId, ipAddress);
                    }
                } catch (Exception e) {}
                
            } else if ("reject".equals(action)) {
                int policyId = Integer.parseInt(req.getParameter("policyId"));
                adminService.rejectPolicy(policyId);
                
                // Логирование
                try {
                    com.example.insurancecompany.service.ActionLogService actionLogService = 
                        (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                    if (actionLogService != null) {
                        String ipAddress = req.getRemoteAddr();
                        actionLogService.logAction(user.getId(), user.getRole(), "REJECT_POLICY", "Policy", policyId, 
                            "Администратор отклонил заявку на полис ID: " + policyId, ipAddress);
                    }
                } catch (Exception e) {}
            }
            resp.sendRedirect(req.getContextPath() + "/admin/policy-requests");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка: " + e.getMessage());
            doGet(req, resp);
        }
    }
}

