package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.Status;
import java.util.List;

public interface StatusDao {
    Status findById(int id) throws Exception;
    Status findByName(String name) throws Exception;
    List<Status> findAll() throws Exception;
}
