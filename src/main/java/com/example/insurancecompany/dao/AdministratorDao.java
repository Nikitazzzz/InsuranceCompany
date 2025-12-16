package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.Administrator;
import java.util.List;

public interface AdministratorDao {
    Administrator findById(int id) throws Exception;
    Administrator findByEmail(String email) throws Exception;
    List<Administrator> findAll() throws Exception;
    void create(Administrator administrator) throws Exception;
    void update(Administrator administrator) throws Exception;
    void delete(int id) throws Exception;
}

