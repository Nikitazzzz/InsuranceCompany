package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.Policy;
import java.util.List;

public interface PolicyDao {
    Policy findById(int id) throws Exception;
    Policy findByPolicyNumber(String policyNumber) throws Exception;
    List<Policy> findByStatusId(int statusId) throws Exception;
    List<Policy> findByOwnerId(int ownerId) throws Exception;
    List<Policy> findAll() throws Exception;
    List<Policy> findByVehicleId(int vehicleId) throws Exception;
    List<Policy> findActive() throws Exception;
    List<Policy> findActiveByOwnerId(int ownerId) throws Exception;
    void create(Policy policy) throws Exception;
    void update(Policy policy) throws Exception;
    void delete(int id) throws Exception;
}
