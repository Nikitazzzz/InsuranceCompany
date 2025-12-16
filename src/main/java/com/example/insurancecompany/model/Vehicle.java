package com.example.insurancecompany.model;

import java.time.LocalDateTime;

public class Vehicle {
    private int id;
    private int ownerId;
    private String vin;
    private String reg;
    private String brand;
    private String model;
    private int yearManufact;
    private int horsePower;
    private String categoryLic;
    private LocalDateTime createdAt;
    private boolean active;

    public Vehicle() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getReg() {
        return reg;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYearManufact() {
        return yearManufact;
    }

    public void setYearManufact(int yearManufact) {
        this.yearManufact = yearManufact;
    }

    public int getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(int horsePower) {
        this.horsePower = horsePower;
    }

    public String getCategoryLic() {
        return categoryLic;
    }

    public void setCategoryLic(String categoryLic) {
        this.categoryLic = categoryLic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
