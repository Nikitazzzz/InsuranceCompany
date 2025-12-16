package com.example.insurancecompany.servlet.UserTasks;

import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Owner;
import com.example.insurancecompany.service.OwnerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class UserHomeServlet extends HttpServlet {

    private OwnerService ownerService;

    @Override
    public void init() throws ServletException {
        this.ownerService = (OwnerService) getServletContext().getAttribute("ownerService");
        if (this.ownerService == null) {
            throw new IllegalStateException("OwnerService not initialized");
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
                try {
                    Owner owner = ownerService.getOwnerById(user.getOwnerId());
                    if (owner != null) {
                        req.setAttribute("owner", owner);
                    }
                } catch (Exception e) {
                    // Логируем ошибку, но не прерываем загрузку страницы
                    System.err.println("Ошибка при получении данных владельца: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            req.getRequestDispatcher("/jsp/userhome.jsp").forward(req, resp);
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при загрузке страницы: " + e.getMessage());
            req.getRequestDispatcher("/jsp/error.jsp").forward(req, resp);
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
        if ("update".equals(action)) {
            try {
                Owner owner = ownerService.getOwnerById(user.getOwnerId());
                if (owner != null) {
                    owner.setOName(req.getParameter("oName"));
                    owner.setSurname(req.getParameter("surname"));
                    owner.setMiddleName(req.getParameter("middleName"));
                    owner.setEmail(req.getParameter("email"));
                    owner.setPhone(req.getParameter("phone"));
                    String birthdayStr = req.getParameter("birthday");
                    if (birthdayStr != null && !birthdayStr.isEmpty()) {
                        owner.setBirthday(java.time.LocalDate.parse(birthdayStr));
                    }
                    owner.setDriverExp(Integer.parseInt(req.getParameter("driverExp")));
                    ownerService.updateOwner(owner);
                    
                    // Логирование действия
                    try {
                        com.example.insurancecompany.service.ActionLogService actionLogService = 
                            (com.example.insurancecompany.service.ActionLogService) req.getServletContext().getAttribute("actionLogService");
                        if (actionLogService != null) {
                            String ipAddress = req.getRemoteAddr();
                            actionLogService.logAction(user.getId(), user.getRole(), "UPDATE", "Owner", owner.getId(), 
                                "Обновление профиля пользователя", ipAddress);
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибки логирования
                    }
                }
                resp.sendRedirect(req.getContextPath() + "/user/home");
            } catch (Exception e) {
                req.setAttribute("error", "Ошибка при обновлении профиля: " + e.getMessage());
                doGet(req, resp);
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/user/home");
        }
    }
}
