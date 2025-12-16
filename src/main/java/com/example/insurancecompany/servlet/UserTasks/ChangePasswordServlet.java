package com.example.insurancecompany.servlet.UserTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.dao.UserAccountDao;
import com.example.insurancecompany.service.ActionLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class ChangePasswordServlet extends HttpServlet {

    private UserAccountDao userAccountDao;
    private ActionLogService actionLogService;

    @Override
    public void init() throws ServletException {
        this.userAccountDao = (UserAccountDao) getServletContext().getAttribute("userAccountDao");
        this.actionLogService = (ActionLogService) getServletContext().getAttribute("actionLogService");
        if (this.userAccountDao == null) {
            throw new IllegalStateException("UserAccountDao not initialized");
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

        req.getRequestDispatcher("/jsp/change_password.jsp").forward(req, resp);
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
        String currentPassword = req.getParameter("currentPassword");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        try {
            // Проверка текущего пароля
            if (!user.getPasswordHash().equals(currentPassword)) {
                req.setAttribute("error", "Неверный текущий пароль");
                req.getRequestDispatcher("/jsp/change_password.jsp").forward(req, resp);
                return;
            }

            // Проверка совпадения новых паролей
            if (!newPassword.equals(confirmPassword)) {
                req.setAttribute("error", "Новые пароли не совпадают");
                req.getRequestDispatcher("/jsp/change_password.jsp").forward(req, resp);
                return;
            }

            // Проверка минимальной длины
            if (newPassword.length() < 6) {
                req.setAttribute("error", "Пароль должен содержать минимум 6 символов");
                req.getRequestDispatcher("/jsp/change_password.jsp").forward(req, resp);
                return;
            }

            // Обновление пароля
            user.setPasswordHash(newPassword); // TODO: использовать BCrypt для хеширования
            userAccountDao.update(user);

            // Логирование
            if (actionLogService != null) {
                try {
                    String ipAddress = req.getRemoteAddr();
                    actionLogService.logAction(user.getId(), user.getRole(), "CHANGE_PASSWORD", "UserAccount", user.getId(), 
                        "Пользователь изменил пароль", ipAddress);
                } catch (Exception e) {}
            }

            req.setAttribute("success", "Пароль успешно изменен");
            req.getRequestDispatcher("/jsp/change_password.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при изменении пароля: " + e.getMessage());
            req.getRequestDispatcher("/jsp/change_password.jsp").forward(req, resp);
        }
    }
}

