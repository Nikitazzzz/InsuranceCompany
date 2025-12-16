package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.Owner;
import java.util.List;

public interface OwnerDao {
    Owner findById(int id) throws Exception;
    Owner findByEmail(String email) throws Exception;
    List<Owner> findAll() throws Exception;
    List<Owner> findByUserId(int userId) throws Exception;
    void create(Owner owner) throws Exception;
    void update(Owner owner) throws Exception;
    void delete(int id) throws Exception;
}
