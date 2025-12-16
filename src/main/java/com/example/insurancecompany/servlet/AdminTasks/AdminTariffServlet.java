package com.example.insurancecompany.servlet.AdminTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Tariff;
import com.example.insurancecompany.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class AdminTariffServlet extends HttpServlet {

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
            List<Tariff> tariffs = adminService.getAllTariffs();
            req.setAttribute("tariffs", tariffs);
            req.getRequestDispatcher("/jsp/admin_tariffs.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException("Ошибка при получении списка тарифов", e);
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
            if ("create".equals(action)) {
                Tariff tariff = new Tariff();
                tariff.setStatId(Integer.parseInt(req.getParameter("statId")));
                tariff.setTariffName(req.getParameter("tariffName"));
                tariff.setPolicyType(req.getParameter("policyType"));
                tariff.setBasePrice(new BigDecimal(req.getParameter("basePrice")));
                tariff.setDescription(req.getParameter("description"));
                // Устанавливаем дефолтные значения для коэффициентов (не используются в расчете)
                tariff.setRegionCoefficient(new BigDecimal("1.0"));
                tariff.setDriverExpCoefficient(new BigDecimal("1.0"));
                tariff.setPowerCoefficient(new BigDecimal("1.0"));
                adminService.createTariff(tariff);
            } else if ("update".equals(action)) {
                Tariff tariff = adminService.getTariffById(Integer.parseInt(req.getParameter("id")));
                if (tariff != null) {
                    tariff.setStatId(Integer.parseInt(req.getParameter("statId")));
                    tariff.setTariffName(req.getParameter("tariffName"));
                    tariff.setPolicyType(req.getParameter("policyType"));
                    tariff.setBasePrice(new BigDecimal(req.getParameter("basePrice")));
                    tariff.setDescription(req.getParameter("description"));
                    // Коэффициенты не изменяются, остаются дефолтными
                    adminService.updateTariff(tariff);
                }
            } else if ("delete".equals(action)) {
                adminService.deleteTariff(Integer.parseInt(req.getParameter("id")));
            }
            resp.sendRedirect(req.getContextPath() + "/admin/tariffs");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка: " + e.getMessage());
            doGet(req, resp);
        }
    }
}
