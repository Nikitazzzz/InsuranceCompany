package com.example.insurancecompany.servlet.UserTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Policy;
import com.example.insurancecompany.model.Vehicle;
import com.example.insurancecompany.model.Tariff;
import com.example.insurancecompany.service.PolicyService;
import com.example.insurancecompany.service.VehicleService;
import com.example.insurancecompany.dao.TariffDao;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class PolicyServlet extends HttpServlet {

    private PolicyService policyService;
    private VehicleService vehicleService;
    private TariffDao tariffDao;

    @Override
    public void init() throws ServletException {
        this.policyService = (PolicyService) getServletContext().getAttribute("policyService");
        this.vehicleService = (VehicleService) getServletContext().getAttribute("vehicleService");
        this.tariffDao = (TariffDao) getServletContext().getAttribute("tariffDao");
        if (this.policyService == null || this.vehicleService == null || this.tariffDao == null) {
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
        try {
            if (user.getOwnerId() != null) {
                List<Policy> policies = policyService.getPoliciesByOwnerId(user.getOwnerId());
                req.setAttribute("policies", policies);
                
                // Для формы создания полиса
                List<Vehicle> vehicles = vehicleService.getVehiclesByOwnerId(user.getOwnerId());
                List<Tariff> tariffs = tariffDao.findActive();
                req.setAttribute("vehicles", vehicles);
                req.setAttribute("tariffs", tariffs);
            }
            req.getRequestDispatcher("/jsp/policies.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Ошибка при получении списка полисов", e);
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

        String action = req.getParameter("action");
        
        // Если это запрос на расчет цены
        if ("calculatePrice".equals(action)) {
            try {
                int vehicleId = Integer.parseInt(req.getParameter("vehicleId"));
                int tariffId = Integer.parseInt(req.getParameter("tariffId"));
                int months = Integer.parseInt(req.getParameter("months"));
                
                BigDecimal price = policyService.getEstimatedPrice(tariffId, user.getOwnerId(), vehicleId, months);
                
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.print("{\"price\":\"" + price + "\"}");
                out.flush();
                return;
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                PrintWriter out = resp.getWriter();
                out.print("{\"error\":\"" + e.getMessage() + "\"}");
                out.flush();
                return;
            }
        }
        
        try {
            int vehicleId = Integer.parseInt(req.getParameter("vehicleId"));
            int tariffId = Integer.parseInt(req.getParameter("tariffId"));
            LocalDate startDate = LocalDate.parse(req.getParameter("startDate"));
            
            // Получаем период полиса из параметра или используем endDate для обратной совместимости
            LocalDate endDate;
            String periodStr = req.getParameter("policyPeriod");
            if (periodStr != null && !periodStr.isEmpty()) {
                int months = Integer.parseInt(periodStr);
                endDate = startDate.plusMonths(months).minusDays(1); // Вычитаем 1 день для точного периода
            } else {
                // Обратная совместимость - если период не указан, используем endDate
                endDate = LocalDate.parse(req.getParameter("endDate"));
            }

            policyService.createPolicy(user.getOwnerId(), vehicleId, tariffId, startDate, endDate);
            session.setAttribute("successMessage", "Заявка на оформление полиса успешно отправлена на рассмотрение администратору");
            resp.sendRedirect(req.getContextPath() + "/user/policies");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка при создании полиса: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
