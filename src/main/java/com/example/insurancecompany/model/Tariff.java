package com.example.insurancecompany.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Tariff {
    private int id;
    private int statId;
    private String tariffName;
    private String policyType; // ОСАГО или КАСКО
    private BigDecimal basePrice;
    private BigDecimal regionCoefficient;
    private BigDecimal driverExpCoefficient;
    private BigDecimal powerCoefficient;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;

    public Tariff() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatId() {
        return statId;
    }

    public void setStatId(int statId) {
        this.statId = statId;
    }

    public String getTariffName() {
        return tariffName;
    }

    public void setTariffName(String tariffName) {
        this.tariffName = tariffName;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getRegionCoefficient() {
        return regionCoefficient;
    }

    public void setRegionCoefficient(BigDecimal regionCoefficient) {
        this.regionCoefficient = regionCoefficient;
    }

    public BigDecimal getDriverExpCoefficient() {
        return driverExpCoefficient;
    }

    public void setDriverExpCoefficient(BigDecimal driverExpCoefficient) {
        this.driverExpCoefficient = driverExpCoefficient;
    }

    public BigDecimal getPowerCoefficient() {
        return powerCoefficient;
    }

    public void setPowerCoefficient(BigDecimal powerCoefficient) {
        this.powerCoefficient = powerCoefficient;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
