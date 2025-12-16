package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.Tariff;
import java.util.List;

public interface TariffDao {
    Tariff findById(int id) throws Exception;
    List<Tariff> findAll() throws Exception;
    List<Tariff> findByPolicyType(String policyType) throws Exception;
    List<Tariff> findActive() throws Exception;
    void create(Tariff tariff) throws Exception;
    void update(Tariff tariff) throws Exception;
    void delete(int id) throws Exception;
}
