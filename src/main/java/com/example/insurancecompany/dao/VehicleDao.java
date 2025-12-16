package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.Vehicle;
import java.util.List;

public interface VehicleDao {
    Vehicle findByRegAndOwner(String reg, int ownerId) throws Exception;
    Vehicle findByVinAndOwner(String vin, int ownerId) throws Exception;
    Vehicle findById(int id) throws Exception;
    List<Vehicle> findByOwnerId(int ownerId) throws Exception;
    Vehicle findByVin(String vin) throws Exception;
    void create(Vehicle vehicle) throws Exception;
    void update(Vehicle vehicle) throws Exception;
    void delete(int id) throws Exception;
}
