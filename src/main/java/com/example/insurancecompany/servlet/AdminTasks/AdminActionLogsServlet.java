package com.example.insurancecompany.servlet.AdminTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.service.ActionLogService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class AdminActionLogsServlet extends HttpServlet {

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
        
        // Проверка: только администратор с id = 1 может просматривать логи
        if (!"ADMIN".equals(user.getRole()) || user.getAdminId() == null || user.getAdminId() != 1) {
            resp.sendRedirect(req.getContextPath() + "/admin/home");
            return;
        }

        try {
            String actionType = req.getParameter("actionType");
            String entityType = req.getParameter("entityType");
            
            java.util.List<com.example.insurancecompany.model.ActionLog> logs;
            if (actionType != null && !actionType.isEmpty()) {
                logs = actionLogService.getLogsByActionType(actionType);
            } else if (entityType != null && !entityType.isEmpty()) {
                logs = actionLogService.getLogsByEntityType(entityType);
            } else {
                logs = actionLogService.getAllLogs();
            }
            
            // Загружаем информацию об администраторах и владельцах для каждого лога
            com.example.insurancecompany.dao.UserAccountDao userAccountDao = 
                (com.example.insurancecompany.dao.UserAccountDao) getServletContext().getAttribute("userAccountDao");
            com.example.insurancecompany.dao.AdministratorDao administratorDao = 
                (com.example.insurancecompany.dao.AdministratorDao) getServletContext().getAttribute("administratorDao");
            com.example.insurancecompany.dao.OwnerDao ownerDao = 
                (com.example.insurancecompany.dao.OwnerDao) getServletContext().getAttribute("ownerDao");
            
            java.util.List<java.util.Map<String, Object>> logsWithUserInfo = new java.util.ArrayList<>();
            for (com.example.insurancecompany.model.ActionLog log : logs) {
                java.util.Map<String, Object> logInfo = new java.util.HashMap<>();
                logInfo.put("log", log);
                
                if (log.getUserId() != null && userAccountDao != null) {
                    try {
                        com.example.insurancecompany.model.UserAccount userAccount = userAccountDao.findById(log.getUserId());
                        if (userAccount != null) {
                            if ("ADMIN".equals(userAccount.getRole()) && userAccount.getAdminId() != null && administratorDao != null) {
                                try {
                                    com.example.insurancecompany.model.Administrator admin = administratorDao.findById(userAccount.getAdminId());
                                    if (admin != null) {
                                        logInfo.put("userName", admin.getName() + " " + admin.getSurname());
                                        logInfo.put("userLogin", userAccount.getLogin());
                                    }
                                } catch (Exception e) {
                                    // Игнорируем ошибки при загрузке администратора
                                }
                            } else if ("OWNER".equals(userAccount.getRole()) && userAccount.getOwnerId() != null && ownerDao != null) {
                                try {
                                    com.example.insurancecompany.model.Owner owner = ownerDao.findById(userAccount.getOwnerId());
                                    if (owner != null) {
                                        logInfo.put("userName", owner.getName() + " " + owner.getSurname());
                                        logInfo.put("userLogin", userAccount.getLogin());
                                    }
                                } catch (Exception e) {
                                    // Игнорируем ошибки при загрузке владельца
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибки при загрузке UserAccount
                    }
                }
                
                logsWithUserInfo.add(logInfo);
            }
            
            req.setAttribute("logs", logsWithUserInfo);
            
            req.getRequestDispatcher("/jsp/admin_action_logs.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при получении логов: " + e.getMessage());
            try {
                req.getRequestDispatcher("/jsp/admin_action_logs.jsp").forward(req, resp);
            } catch (Exception ex) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ошибка сервера");
            }
        }
    }
}

