package com.example.insurancecompany.servlet.auth;


import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.service.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;

import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() throws ServletException {
        this.authService = (AuthService) getServletContext().getAttribute("authService");
        if (this.authService == null) {
            throw new IllegalStateException("AuthService not initialized");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        try {
            UserAccount user = authService.authenticate(login, password);
            if (user == null) {
                req.setAttribute("error", "Неверный логин или пароль");
                req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", user);

            if ("ADMIN".equals(user.getRole())) {
                resp.sendRedirect(req.getContextPath() + "/admin/home");
            } else {
                resp.sendRedirect(req.getContextPath() + "/user/home");
            }

        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Ошибка при аутентификации: " + (e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName()));
            req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
        }
    }
}
