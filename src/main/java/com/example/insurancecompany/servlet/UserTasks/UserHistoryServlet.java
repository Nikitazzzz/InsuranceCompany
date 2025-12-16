package com.example.insurancecompany.servlet.UserTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.service.ActionLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class UserHistoryServlet extends HttpServlet {

    private ActionLogService actionLogService;

    @Override
    public void init() throws ServletException {
        this.actionLogService = (ActionLogService) getServletContext().getAttribute("actionLogService");
        if (this.actionLogService == null) {
            throw new IllegalStateException("ActionLogService not initialized");
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
            // Получаем историю операций пользователя
            req.setAttribute("logs", actionLogService.getLogsByUserId(user.getId()));
            req.getRequestDispatcher("/jsp/user_history.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при получении истории операций: " + e.getMessage());
            try {
                req.getRequestDispatcher("/jsp/user_history.jsp").forward(req, resp);
            } catch (Exception ex) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
        }
    }
}

