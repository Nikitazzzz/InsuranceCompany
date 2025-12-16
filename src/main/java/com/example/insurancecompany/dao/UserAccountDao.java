package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.UserAccount;

public interface UserAccountDao {
    UserAccount findByLogin(String login) throws Exception;
    UserAccount findById(int id) throws Exception;
    void create(UserAccount user) throws Exception;
    void update(UserAccount user) throws Exception;
}
