package com.example.insurancecompany.service;

import com.example.insurancecompany.dao.TariffDao;
import com.example.insurancecompany.dao.InsuranceDao;
import com.example.insurancecompany.dao.PayoutDao;
import com.example.insurancecompany.dao.AdministratorDao;
import com.example.insurancecompany.dao.UserAccountDao;
import com.example.insurancecompany.dao.PolicyDao;
import com.example.insurancecompany.dao.OwnerDao;
import com.example.insurancecompany.dao.VehicleDao;
import com.example.insurancecompany.model.Tariff;
import com.example.insurancecompany.model.InsuranceCase;
import com.example.insurancecompany.model.Payout;
import com.example.insurancecompany.model.Administrator;
import com.example.insurancecompany.model.UserAccount;
import com.example.insurancecompany.model.Policy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AdminService {
    private final TariffDao tariffDao;
    private final InsuranceDao insuranceDao;
    private final PayoutDao payoutDao;
    private final AdministratorDao administratorDao;
    private final UserAccountDao userAccountDao;
    private final PolicyDao policyDao;
    private final OwnerDao ownerDao;
    private final VehicleDao vehicleDao;

    public AdminService(TariffDao tariffDao, InsuranceDao insuranceDao, PayoutDao payoutDao, 
                        AdministratorDao administratorDao, UserAccountDao userAccountDao,
                        PolicyDao policyDao, OwnerDao ownerDao, VehicleDao vehicleDao) {
        this.tariffDao = tariffDao;
        this.insuranceDao = insuranceDao;
        this.payoutDao = payoutDao;
        this.administratorDao = administratorDao;
        this.userAccountDao = userAccountDao;
        this.policyDao = policyDao;
        this.ownerDao = ownerDao;
        this.vehicleDao = vehicleDao;
    }

    // Управление тарифами
    public List<Tariff> getAllTariffs() throws Exception {
        return tariffDao.findAll();
    }

    public Tariff getTariffById(int id) throws Exception {
        return tariffDao.findById(id);
    }

    public void createTariff(Tariff tariff) throws Exception {
        tariff.setActive(true);
        tariff.setCreatedAt(LocalDateTime.now());
        // Устанавливаем дефолтные значения для коэффициентов (не используются в расчете)
        if (tariff.getRegionCoefficient() == null) {
            tariff.setRegionCoefficient(new java.math.BigDecimal("1.0"));
        }
        if (tariff.getDriverExpCoefficient() == null) {
            tariff.setDriverExpCoefficient(new java.math.BigDecimal("1.0"));
        }
        if (tariff.getPowerCoefficient() == null) {
            tariff.setPowerCoefficient(new java.math.BigDecimal("1.0"));
        }
        tariffDao.create(tariff);
    }

    public void updateTariff(Tariff tariff) throws Exception {
        tariffDao.update(tariff);
    }

    public void deleteTariff(int id) throws Exception {
        tariffDao.delete(id);
    }

    // Управление страховыми случаями
    public List<InsuranceCase> getAllInsuranceCases() throws Exception {
        return insuranceDao.findAll();
    }

    public List<InsuranceCase> getInsuranceCasesByStatus(int statusId) throws Exception {
        return insuranceDao.findByStatusId(statusId);
    }

    public InsuranceCase getInsuranceCaseById(int id) throws Exception {
        return insuranceDao.findById(id);
    }

    public void approveInsuranceCase(int id, String comment) throws Exception {
        InsuranceCase insuranceCase = insuranceDao.findById(id);
        if (insuranceCase != null) {
            insuranceCase.setStatId(4); // Одобрен
            insuranceCase.setAdminComment(comment);
            insuranceCase.setDecisionDate(LocalDateTime.now());
            insuranceDao.update(insuranceCase);
            
            // Деактивируем полис после одобрения страхового случая
            Policy policy = policyDao.findById(insuranceCase.getPolicyId());
            if (policy != null) {
                policy.setActive(false);
                policyDao.update(policy);
            }
        }
    }

    public void rejectInsuranceCase(int id, String comment) throws Exception {
        InsuranceCase insuranceCase = insuranceDao.findById(id);
        if (insuranceCase != null) {
            insuranceCase.setStatId(5); // Отклонен
            insuranceCase.setAdminComment(comment);
            insuranceCase.setDecisionDate(LocalDateTime.now());
            insuranceDao.update(insuranceCase);
        }
    }

    // Управление выплатами
    public void createPayout(int insuranceId, int adminId, BigDecimal sumPayout, String paymentMethod) throws Exception {
        Payout payout = new Payout();
        payout.setInsuranceId(insuranceId);
        payout.setAdminId(adminId);
        payout.setSumPayout(sumPayout);
        payout.setPaymentMethod(paymentMethod);
        payout.setPayoutDate(LocalDateTime.now());
        payout.setActive(true);
        payoutDao.create(payout);

        // Обновляем статус страхового случая на "Оплачен"
        InsuranceCase insuranceCase = insuranceDao.findById(insuranceId);
        if (insuranceCase != null) {
            insuranceCase.setStatId(8); // Оплачен
            insuranceDao.update(insuranceCase);
        }
    }

    public List<Payout> getAllPayouts() throws Exception {
        return payoutDao.findAll();
    }

    // Управление администраторами (только для администратора с id = 1)
    public List<Administrator> getAllAdministrators() throws Exception {
        return administratorDao.findAll();
    }

    public Administrator getAdministratorById(int id) throws Exception {
        return administratorDao.findById(id);
    }

    public void createAdministrator(Administrator administrator, String login, String password) throws Exception {
        // Проверяем, что логин уникален
        if (login == null || login.trim().isEmpty()) {
            throw new Exception("Логин не может быть пустым");
        }
        if (userAccountDao.findByLogin(login) != null) {
            throw new Exception("Пользователь с таким логином уже существует");
        }

        // Проверяем, что email уникален (если указан)
        if (administrator.getEmail() != null && !administrator.getEmail().trim().isEmpty()) {
            Administrator existing = administratorDao.findByEmail(administrator.getEmail());
            if (existing != null) {
                throw new Exception("Администратор с таким email уже существует");
            }
        }

        // Создаем администратора
        administrator.setActive(true);
        administrator.setCreatedAt(LocalDateTime.now());
        administratorDao.create(administrator);

        // Создаем учетную запись пользователя для администратора
        UserAccount userAccount = new UserAccount();
        userAccount.setLogin(login.trim());
        userAccount.setPasswordHash(password); // TODO: использовать BCrypt для хеширования
        userAccount.setRole("ADMIN");
        userAccount.setAdminId(administrator.getId());
        userAccount.setOwnerId(null);
        userAccount.setActive(true);
        userAccount.setCreatedAt(LocalDateTime.now());
        userAccountDao.create(userAccount);
    }

    public void updateAdministrator(Administrator administrator) throws Exception {
        administratorDao.update(administrator);
    }

    public void deleteAdministrator(int id) throws Exception {
        administratorDao.delete(id);
    }

    // Управление заявками на полисы
    public List<Policy> getPendingPolicies() throws Exception {
        return policyDao.findByStatusId(3); // В обработке
    }

    public Policy getPolicyById(int id) throws Exception {
        return policyDao.findById(id);
    }

    public void approvePolicy(int id) throws Exception {
        Policy policy = policyDao.findById(id);
        if (policy != null && policy.getStatId() == 3) {
            policy.setStatId(1); // Активный
            policyDao.update(policy);
        }
    }

    public void rejectPolicy(int id) throws Exception {
        Policy policy = policyDao.findById(id);
        if (policy != null && policy.getStatId() == 3) {
            policy.setStatId(5); // Отклонен
            policyDao.update(policy);
        }
    }
}
