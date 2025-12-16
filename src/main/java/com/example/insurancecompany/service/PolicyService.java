package com.example.insurancecompany.service;

import com.example.insurancecompany.dao.PolicyDao;
import com.example.insurancecompany.dao.TariffDao;
import com.example.insurancecompany.dao.OwnerDao;
import com.example.insurancecompany.dao.VehicleDao;
import com.example.insurancecompany.model.Policy;
import com.example.insurancecompany.model.Tariff;
import com.example.insurancecompany.model.Owner;
import com.example.insurancecompany.model.Vehicle;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PolicyService {
    private final PolicyDao policyDao;
    private final TariffDao tariffDao;
    private final OwnerDao ownerDao;
    private final VehicleDao vehicleDao;

    public PolicyService(PolicyDao policyDao, TariffDao tariffDao, OwnerDao ownerDao, VehicleDao vehicleDao) {
        this.policyDao = policyDao;
        this.tariffDao = tariffDao;
        this.ownerDao = ownerDao;
        this.vehicleDao = vehicleDao;
    }

    public BigDecimal calculatePolicyPrice(int tariffId, int ownerId, int vehicleId, int months) throws Exception {
        Tariff tariff = tariffDao.findById(tariffId);
        if (tariff == null) {
            throw new Exception("Тариф не найден");
        }

        Owner owner = ownerDao.findById(ownerId);
        if (owner == null) {
            throw new Exception("Владелец не найден");
        }

        Vehicle vehicle = vehicleDao.findById(vehicleId);
        if (vehicle == null) {
            throw new Exception("Транспортное средство не найдено");
        }

        // Базовая цена тарифа указана за месяц
        BigDecimal basePricePerMonth = tariff.getBasePrice();
        
        // Динамический расчет коэффициента стажа водителя
        // Чем больше стаж, тем меньше коэффициент (скидка)
        BigDecimal driverExpCoeff = calculateDriverExpCoefficient(owner.getDriverExp());
        
        // Динамический расчет коэффициента мощности
        // Чем больше мощность, тем больше коэффициент (надбавка)
        BigDecimal powerCoeff = calculatePowerCoefficient(vehicle.getHorsePower());

        // Расчет цены за месяц: базовая цена * коэффициент стажа * коэффициент мощности
        BigDecimal pricePerMonth = basePricePerMonth
                .multiply(driverExpCoeff)
                .multiply(powerCoeff);

        // Итоговая цена = цена за месяц * количество месяцев
        BigDecimal totalPrice = pricePerMonth.multiply(new BigDecimal(months));

        // Округляем до 2 знаков после запятой
        totalPrice = totalPrice.setScale(2, java.math.RoundingMode.HALF_UP);

        return totalPrice;
    }

    // Перегрузка метода для обратной совместимости (использует 1 месяц по умолчанию)
    public BigDecimal calculatePolicyPrice(int tariffId, int ownerId, int vehicleId) throws Exception {
        return calculatePolicyPrice(tariffId, ownerId, vehicleId, 1);
    }

    /**
     * Расчет коэффициента на основе стажа водителя
     * Логика:
     * - 0-2 года: коэффициент 1.5 (надбавка 50%)
     * - 3-5 лет: коэффициент 1.2 (надбавка 20%)
     * - 6-10 лет: коэффициент 1.0 (базовый)
     * - 11-15 лет: коэффициент 0.9 (скидка 10%)
     * - 16+ лет: коэффициент 0.8 (скидка 20%)
     */
    private BigDecimal calculateDriverExpCoefficient(int driverExp) {
        if (driverExp <= 2) {
            return new BigDecimal("1.5");
        } else if (driverExp <= 5) {
            return new BigDecimal("1.2");
        } else if (driverExp <= 10) {
            return new BigDecimal("1.0");
        } else if (driverExp <= 15) {
            return new BigDecimal("0.9");
        } else {
            return new BigDecimal("0.8");
        }
    }

    /**
     * Расчет коэффициента на основе мощности автомобиля
     * Логика:
     * - До 100 л.с.: коэффициент 0.8 (скидка 20%)
     * - 101-150 л.с.: коэффициент 1.0 (базовый)
     * - 151-200 л.с.: коэффициент 1.3 (надбавка 30%)
     * - 201-250 л.с.: коэффициент 1.6 (надбавка 60%)
     * - 251+ л.с.: коэффициент 2.0 (надбавка 100%)
     */
    private BigDecimal calculatePowerCoefficient(int horsePower) {
        if (horsePower <= 100) {
            return new BigDecimal("0.8");
        } else if (horsePower <= 150) {
            return new BigDecimal("1.0");
        } else if (horsePower <= 200) {
            return new BigDecimal("1.3");
        } else if (horsePower <= 250) {
            return new BigDecimal("1.6");
        } else {
            return new BigDecimal("2.0");
        }
    }

    public Policy createPolicy(int ownerId, int vehicleId, int tariffId, LocalDate startDate, LocalDate endDate) throws Exception {
        // Вычисляем количество месяцев между датами
        int months = (int) java.time.temporal.ChronoUnit.MONTHS.between(
            startDate.withDayOfMonth(1), 
            endDate.withDayOfMonth(1)
        );
        if (months < 1) {
            months = 1; // Минимум 1 месяц
        }
        
        BigDecimal price = calculatePolicyPrice(tariffId, ownerId, vehicleId, months);
        
        Policy policy = new Policy();
        policy.setOwnerId(ownerId);
        policy.setVehicleId(vehicleId);
        policy.setTariffId(tariffId);
        policy.setStatId(3); // В обработке (ожидает подтверждения администратора)
        policy.setPolicyNumber(generatePolicyNumber());
        policy.setStartDate(startDate);
        policy.setEndDate(endDate);
        policy.setPrice(price);
        policy.setCreatedAt(LocalDateTime.now());
        policy.setActive(true);

        policyDao.create(policy);
        return policy;
    }

    public List<Policy> getPoliciesByOwnerId(int ownerId) throws Exception {
        return policyDao.findByOwnerId(ownerId);
    }

    public List<Policy> getActivePoliciesByOwnerId(int ownerId) throws Exception {
        return policyDao.findActiveByOwnerId(ownerId);
    }

    public Policy getPolicyById(int id) throws Exception {
        return policyDao.findById(id);
    }

    /**
     * Получить предварительную цену полиса без его создания
     */
    public BigDecimal getEstimatedPrice(int tariffId, int ownerId, int vehicleId, int months) throws Exception {
        return calculatePolicyPrice(tariffId, ownerId, vehicleId, months);
    }

    private String generatePolicyNumber() {
        return "POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
