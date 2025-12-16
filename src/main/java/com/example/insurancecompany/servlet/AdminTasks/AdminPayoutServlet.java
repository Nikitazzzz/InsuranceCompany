package com.example.insurancecompany.servlet.AdminTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Payout;
import com.example.insurancecompany.model.InsuranceCase;
import com.example.insurancecompany.model.Policy;
import com.example.insurancecompany.model.Vehicle;
import com.example.insurancecompany.model.Tariff;
import com.example.insurancecompany.model.Owner;
import com.example.insurancecompany.service.AdminService;
import com.example.insurancecompany.dao.InsuranceDao;
import com.example.insurancecompany.dao.PolicyDao;
import com.example.insurancecompany.dao.VehicleDao;
import com.example.insurancecompany.dao.TariffDao;
import com.example.insurancecompany.dao.OwnerDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPayoutServlet extends HttpServlet {

    private AdminService adminService;
    private InsuranceDao insuranceDao;
    private PolicyDao policyDao;
    private VehicleDao vehicleDao;
    private TariffDao tariffDao;
    private OwnerDao ownerDao;

    @Override
    public void init() throws ServletException {
        this.adminService = (AdminService) getServletContext().getAttribute("adminService");
        this.insuranceDao = (InsuranceDao) getServletContext().getAttribute("insuranceDao");
        this.policyDao = (PolicyDao) getServletContext().getAttribute("policyDao");
        this.vehicleDao = (VehicleDao) getServletContext().getAttribute("vehicleDao");
        this.tariffDao = (TariffDao) getServletContext().getAttribute("tariffDao");
        this.ownerDao = (OwnerDao) getServletContext().getAttribute("ownerDao");
        if (this.adminService == null || this.insuranceDao == null || this.policyDao == null || 
            this.vehicleDao == null || this.tariffDao == null || this.ownerDao == null) {
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
            List<Payout> payouts = adminService.getAllPayouts();
            req.setAttribute("payouts", payouts);
            
            // Для формы создания выплаты - только одобренные страховые случаи
            List<InsuranceCase> approvedCases = adminService.getInsuranceCasesByStatus(4);
            
            // Создаем список с дополнительной информацией о страховых случаях
            List<Map<String, Object>> casesWithDetails = new ArrayList<>();
            for (InsuranceCase insuranceCase : approvedCases) {
                Map<String, Object> caseInfo = new HashMap<>();
                caseInfo.put("insuranceCase", insuranceCase);
                
                Policy policy = policyDao.findById(insuranceCase.getPolicyId());
                if (policy != null) {
                    caseInfo.put("policy", policy);
                    
                    Vehicle vehicle = vehicleDao.findById(policy.getVehicleId());
                    Tariff tariff = tariffDao.findById(policy.getTariffId());
                    Owner owner = ownerDao.findById(insuranceCase.getOwnerId());
                    
                    if (vehicle != null) {
                        caseInfo.put("vehicle", vehicle);
                    }
                    if (tariff != null) {
                        caseInfo.put("tariff", tariff);
                    }
                    if (owner != null) {
                        caseInfo.put("owner", owner);
                    }
                }
                
                casesWithDetails.add(caseInfo);
            }
            
            req.setAttribute("approvedCases", casesWithDetails);
            
            req.getRequestDispatcher("/jsp/admin_payouts.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при получении списка выплат: " + 
                (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            try {
                req.getRequestDispatcher("/jsp/admin_payouts.jsp").forward(req, resp);
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

        try {
            int insuranceId = Integer.parseInt(req.getParameter("insuranceId"));
            int adminId = user.getAdminId() != null ? user.getAdminId() : 1; // TODO: получить реальный adminId
            BigDecimal sumPayout = new BigDecimal(req.getParameter("sumPayout"));
            String paymentMethod = req.getParameter("paymentMethod");

            adminService.createPayout(insuranceId, adminId, sumPayout, paymentMethod);
            
            // Логирование
            try {
                com.example.insurancecompany.service.ActionLogService actionLogService = 
                    (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                if (actionLogService != null) {
                    String ipAddress = req.getRemoteAddr();
                    actionLogService.logAction(user.getId(), user.getRole(), "CREATE_PAYOUT", "Payout", insuranceId, 
                        "Администратор создал выплату для страхового случая ID: " + insuranceId + ". Сумма: " + sumPayout + " ₽. Способ оплаты: " + paymentMethod, ipAddress);
                }
            } catch (Exception e) {}
            
            resp.sendRedirect(req.getContextPath() + "/admin/payouts");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка при создании выплаты: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
