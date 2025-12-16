package com.example.insurancecompany.model;

public class Status {
    private int id;
    private String stName;

    public Status() {
    }

    public Status(int id, String stName) {
        this.id = id;
        this.stName = stName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStName() {
        return stName;
    }

    public void setStName(String stName) {
        this.stName = stName;
    }
}
