package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.InsuranceCase;
import java.util.List;

public interface InsuranceDao {
    InsuranceCase findById(int id) throws Exception;
    List<InsuranceCase> findByOwnerId(int ownerId) throws Exception;
    List<InsuranceCase> findByPolicyId(int policyId) throws Exception;
    List<InsuranceCase> findByStatusId(int statusId) throws Exception;
    List<InsuranceCase> findAll() throws Exception;
    void create(InsuranceCase insuranceCase) throws Exception;
    void update(InsuranceCase insuranceCase) throws Exception;
    void delete(int id) throws Exception;
}
