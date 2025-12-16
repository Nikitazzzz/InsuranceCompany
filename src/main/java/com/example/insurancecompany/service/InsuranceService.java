package com.example.insurancecompany.service;

import com.example.insurancecompany.dao.InsuranceDao;
import com.example.insurancecompany.model.InsuranceCase;
import java.time.LocalDateTime;
import java.util.List;

public class InsuranceService {
    private final InsuranceDao insuranceDao;

    public InsuranceService(InsuranceDao insuranceDao) {
        this.insuranceDao = insuranceDao;
    }

    public void createInsuranceCase(InsuranceCase insuranceCase) throws Exception {
        insuranceCase.setStatId(3); // В обработке
        insuranceCase.setCreateDate(LocalDateTime.now());
        insuranceCase.setActive(true);
        insuranceDao.create(insuranceCase);
    }

    public List<InsuranceCase> getInsuranceCasesByOwnerId(int ownerId) throws Exception {
        return insuranceDao.findByOwnerId(ownerId);
    }

    public List<InsuranceCase> getAllInsuranceCases() throws Exception {
        return insuranceDao.findAll();
    }

    public List<InsuranceCase> getInsuranceCasesByStatus(int statusId) throws Exception {
        return insuranceDao.findByStatusId(statusId);
    }

    public InsuranceCase getInsuranceCaseById(int id) throws Exception {
        return insuranceDao.findById(id);
    }

    public void updateInsuranceCase(InsuranceCase insuranceCase) throws Exception {
        insuranceDao.update(insuranceCase);
    }

    public void approveInsuranceCase(int id, String adminComment) throws Exception {
        InsuranceCase insuranceCase = insuranceDao.findById(id);
        if (insuranceCase != null) {
            insuranceCase.setStatId(4); // Одобрен
            insuranceCase.setAdminComment(adminComment);
            insuranceCase.setDecisionDate(LocalDateTime.now());
            insuranceDao.update(insuranceCase);
        }
    }

    public void rejectInsuranceCase(int id, String adminComment) throws Exception {
        InsuranceCase insuranceCase = insuranceDao.findById(id);
        if (insuranceCase != null) {
            insuranceCase.setStatId(5); // Отклонен
            insuranceCase.setAdminComment(adminComment);
            insuranceCase.setDecisionDate(LocalDateTime.now());
            insuranceDao.update(insuranceCase);
        }
    }
}
