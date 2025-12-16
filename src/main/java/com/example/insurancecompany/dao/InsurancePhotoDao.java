package com.example.insurancecompany.dao;

import com.example.insurancecompany.model.InsurancePhoto;
import java.util.List;

public interface InsurancePhotoDao {
    void create(InsurancePhoto photo) throws Exception;
    List<InsurancePhoto> findByInsuranceId(int insuranceId) throws Exception;
    InsurancePhoto findById(int id) throws Exception;
    void delete(int id) throws Exception;
}

