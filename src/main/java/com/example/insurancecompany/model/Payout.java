package com.example.insurancecompany.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payout {
    private int id;
    private int insuranceId;
    private int adminId;
    private BigDecimal sumPayout;
    private LocalDateTime payoutDate;
    private String paymentMethod; // Наличные или На карту
    private boolean active;

    public Payout() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(int insuranceId) {
        this.insuranceId = insuranceId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public BigDecimal getSumPayout() {
        return sumPayout;
    }

    public void setSumPayout(BigDecimal sumPayout) {
        this.sumPayout = sumPayout;
    }

    public LocalDateTime getPayoutDate() {
        return payoutDate;
    }

    public void setPayoutDate(LocalDateTime payoutDate) {
        this.payoutDate = payoutDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
