package com.example.insurancecompany.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Owner {
    private int id;
    private String email;
    private String oName;
    private String surname;
    private String middleName;
    private String phone;
    private LocalDate birthday;
    private int driverExp;
    private LocalDateTime createdAt;
    private boolean active;

    public Owner() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOName() {
        return oName;
    }

    public void setOName(String oName) {
        this.oName = oName;
    }

    // Вспомогательный метод для EL (Expression Language)
    public String getName() {
        return oName;
    }
    
    // Метод для форматирования даты рождения для JSP
    public String getFormattedBirthday() {
        if (birthday == null) {
            return null;
        }
        return birthday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public int getDriverExp() {
        return driverExp;
    }

    public void setDriverExp(int driverExp) {
        this.driverExp = driverExp;
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
