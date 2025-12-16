package com.example.insurancecompany.servlet.AdminTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Policy;
import com.example.insurancecompany.model.Owner;
import com.example.insurancecompany.model.Vehicle;
import com.example.insurancecompany.model.Tariff;
import com.example.insurancecompany.service.AdminService;
import com.example.insurancecompany.dao.PolicyDao;
import com.example.insurancecompany.dao.OwnerDao;
import com.example.insurancecompany.dao.VehicleDao;
import com.example.insurancecompany.dao.TariffDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPoliciesServlet extends HttpServlet {

    private AdminService adminService;
    private PolicyDao policyDao;
    private OwnerDao ownerDao;
    private VehicleDao vehicleDao;
    private TariffDao tariffDao;

    @Override
    public void init() throws ServletException {
        this.adminService = (AdminService) getServletContext().getAttribute("adminService");
        this.policyDao = (PolicyDao) getServletContext().getAttribute("policyDao");
        this.ownerDao = (OwnerDao) getServletContext().getAttribute("ownerDao");
        this.vehicleDao = (VehicleDao) getServletContext().getAttribute("vehicleDao");
        this.tariffDao = (TariffDao) getServletContext().getAttribute("tariffDao");
        if (this.adminService == null || this.policyDao == null) {
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
            String search = req.getParameter("search");
            List<Policy> policies;
            
            if (search != null && !search.isEmpty()) {
                // Поиск по номеру полиса
                Policy policy = policyDao.findByPolicyNumber(search);
                policies = new ArrayList<>();
                if (policy != null) {
                    policies.add(policy);
                }
            } else {
                policies = policyDao.findAll();
            }
            
            // Создаем список с дополнительной информацией
            List<Map<String, Object>> policiesWithDetails = new ArrayList<>();
            for (Policy policy : policies) {
                Map<String, Object> policyInfo = new HashMap<>();
                policyInfo.put("policy", policy);
                
                Owner owner = ownerDao.findById(policy.getOwnerId());
                Vehicle vehicle = vehicleDao.findById(policy.getVehicleId());
                Tariff tariff = tariffDao.findById(policy.getTariffId());
                
                policyInfo.put("owner", owner);
                policyInfo.put("vehicle", vehicle);
                policyInfo.put("tariff", tariff);
                
                policiesWithDetails.add(policyInfo);
            }
            
            req.setAttribute("policies", policiesWithDetails);
            req.getRequestDispatcher("/jsp/admin_policies.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при получении списка полисов: " + e.getMessage());
            try {
                req.getRequestDispatcher("/jsp/admin_policies.jsp").forward(req, resp);
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
            int policyId = Integer.parseInt(req.getParameter("policyId"));
            Policy policy = policyDao.findById(policyId);
            
            if (policy == null) {
                req.setAttribute("error", "Полис не найден");
                doGet(req, resp);
                return;
            }
            
            if ("extend".equals(action)) {
                // Продление полиса на 1 год
                LocalDate newEndDate = policy.getEndDate().plusYears(1);
                policy.setEndDate(newEndDate);
                policyDao.update(policy);
                
                // Логирование
                try {
                    com.example.insurancecompany.service.ActionLogService actionLogService = 
                        (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                    if (actionLogService != null) {
                        String ipAddress = req.getRemoteAddr();
                        actionLogService.logAction(user.getId(), user.getRole(), "EXTEND_POLICY", "Policy", policy.getId(), 
                            "Администратор продлил полис: " + policy.getPolicyNumber() + ". Новый срок действия: " + newEndDate, ipAddress);
                    }
                } catch (Exception e) {}
                
            } else if ("cancel".equals(action)) {
                // Аннулирование полиса
                policy.setActive(false);
                policy.setStatId(2); // Неактивный
                policyDao.update(policy);
                
                // Логирование
                try {
                    com.example.insurancecompany.service.ActionLogService actionLogService = 
                        (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                    if (actionLogService != null) {
                        String ipAddress = req.getRemoteAddr();
                        actionLogService.logAction(user.getId(), user.getRole(), "CANCEL_POLICY", "Policy", policy.getId(), 
                            "Администратор аннулировал полис: " + policy.getPolicyNumber(), ipAddress);
                    }
                } catch (Exception e) {}
            }
            
            resp.sendRedirect(req.getContextPath() + "/admin/policies");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка: " + e.getMessage());
            doGet(req, resp);
        }
    }
}

