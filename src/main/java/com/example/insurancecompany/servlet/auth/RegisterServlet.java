package com.example.insurancecompany.servlet.auth;

import com.example.insurancecompany.dao.UserAccountDao;
import com.example.insurancecompany.dao.OwnerDao;
import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Owner;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegisterServlet extends HttpServlet {

    private UserAccountDao userDao;
    private OwnerDao ownerDao;

    @Override
    public void init() throws ServletException {
        this.userDao = (UserAccountDao) getServletContext().getAttribute("userAccountDao");
        this.ownerDao = (OwnerDao) getServletContext().getAttribute("ownerDao");
        if (this.userDao == null || this.ownerDao == null) {
            throw new IllegalStateException("DAO not initialized");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String oName = req.getParameter("oName");
        String surname = req.getParameter("surname");
        String middleName = req.getParameter("middleName");
        String phone = req.getParameter("phone");
        String birthdayStr = req.getParameter("birthday");
        String driverExpStr = req.getParameter("driverExp");

        if (login == null || login.isBlank() || password == null || password.isBlank()) {
            req.setAttribute("error", "Логин и пароль обязательны");
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
            return;
        }

        try {
            // проверка уникальности логина
            if (userDao.findByLogin(login) != null) {
                req.setAttribute("error", "Пользователь с таким логином уже существует");
                req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
                return;
            }

            // Создание владельца
            Owner owner = new Owner();
            owner.setEmail(email != null ? email : "");
            owner.setOName(oName != null ? oName : "");
            owner.setSurname(surname != null ? surname : "");
            owner.setMiddleName(middleName);
            owner.setPhone(phone != null ? phone : "");
            owner.setBirthday(birthdayStr != null && !birthdayStr.isBlank() 
                ? LocalDate.parse(birthdayStr) : LocalDate.now());
            owner.setDriverExp(driverExpStr != null && !driverExpStr.isBlank() 
                ? Integer.parseInt(driverExpStr) : 0);
            owner.setCreatedAt(LocalDateTime.now());
            owner.setActive(true);
            ownerDao.create(owner);

            // Создание пользователя
            UserAccount user = new UserAccount();
            user.setLogin(login);
            user.setPasswordHash(password); // TODO: заменить на хеш (BCrypt)
            user.setRole("OWNER");
            user.setOwnerId(owner.getId());
            user.setAdminId(null);
            user.setActive(true);
            user.setCreatedAt(LocalDateTime.now());
            userDao.create(user);

            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (Exception e) {
            req.setAttribute("error", "Ошибка при регистрации: " + e.getMessage());
            req.getRequestDispatcher("/jsp/register.jsp").forward(req, resp);
        }
    }
}
