package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.Payout;
import java.util.List;

public interface PayoutDao {
    Payout findById(int id) throws Exception;
    Payout findByInsuranceId(int insuranceId) throws Exception;
    List<Payout> findAll() throws Exception;
    List<Payout> findByAdminId(int adminId) throws Exception;
    void create(Payout payout) throws Exception;
    void update(Payout payout) throws Exception;
    void delete(int id) throws Exception;
}
