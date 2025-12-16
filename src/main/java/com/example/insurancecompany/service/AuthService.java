package com.example.insurancecompany.service;

import com.example.insurancecompany.dao.UserAccountDao;
import com.example.insurancecompany.model.UserAccount;

public class AuthService {

    private final UserAccountDao userDao;

    public AuthService(UserAccountDao userDao) {
        this.userDao = userDao;
    }

    public UserAccount authenticate(String login, String password) throws Exception {
        UserAccount user = userDao.findByLogin(login);
        if (user == null || !user.isActive()) {
            return null;
        }
        // TODO: сравнивать хеш (BCrypt). Сейчас – прямое сравнение:
        if (!user.getPasswordHash().equals(password)) {
            return null;
        }
        return user;
    }

    public boolean loginExists(String login) throws Exception {
        return userDao.findByLogin(login) != null;
    }
}
