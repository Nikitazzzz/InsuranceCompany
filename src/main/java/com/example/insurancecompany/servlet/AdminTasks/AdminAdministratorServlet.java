package com.example.insurancecompany.servlet.AdminTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Administrator;
import com.example.insurancecompany.service.AdminService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

public class AdminAdministratorServlet extends HttpServlet {

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

        // Проверяем, что это администратор с id = 1
        if (user.getAdminId() == null || user.getAdminId() != 1) {
            req.setAttribute("error", "У вас нет прав для управления администраторами");
            req.getRequestDispatcher("/jsp/admindashboard.jsp").forward(req, resp);
            return;
        }

        try {
            List<Administrator> administrators = adminService.getAllAdministrators();
            req.setAttribute("administrators", administrators);
            req.getRequestDispatcher("/jsp/admin_administrators.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при получении списка администраторов: " + e.getMessage());
            try {
                req.getRequestDispatcher("/jsp/admin_administrators.jsp").forward(req, resp);
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

        // Проверяем, что это администратор с id = 1
        if (user.getAdminId() == null || user.getAdminId() != 1) {
            req.setAttribute("error", "У вас нет прав для управления администраторами");
            doGet(req, resp);
            return;
        }

        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                Administrator administrator = new Administrator();
                administrator.setName(req.getParameter("aName"));
                administrator.setSurname(req.getParameter("surname"));
                administrator.setPosition(req.getParameter("position"));
                administrator.setWorkExp(Integer.parseInt(req.getParameter("workExp")));
                administrator.setEmail(req.getParameter("email"));
                
                String login = req.getParameter("login");
                String password = req.getParameter("password");
                
                if (login == null || login.isEmpty() || password == null || password.isEmpty()) {
                    req.setAttribute("error", "Логин и пароль обязательны для заполнения");
                    doGet(req, resp);
                    return;
                }
                
                adminService.createAdministrator(administrator, login, password);
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                adminService.deleteAdministrator(id);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/administrators");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка: " + e.getMessage());
            doGet(req, resp);
        }
    }
}

