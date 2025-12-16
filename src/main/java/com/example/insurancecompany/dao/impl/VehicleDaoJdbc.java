package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.VehicleDao;
import com.example.insurancecompany.model.Vehicle;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VehicleDaoJdbc implements VehicleDao {

    @Override
    public Vehicle findById(int id) throws Exception {
        String sql = "SELECT ID, OwnerID, VIN, Reg, Brand, Model, YearManufact, HorsePower, CategoryLic, CreatedAt, IsActive " +
                     "FROM Vehicle WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Vehicle> findByOwnerId(int ownerId) throws Exception {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT ID, OwnerID, VIN, Reg, Brand, Model, YearManufact, HorsePower, CategoryLic, CreatedAt, IsActive " +
                     "FROM Vehicle WHERE OwnerID = ? AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapRow(rs));
                }
            }
        }
        return vehicles;
    }

    @Override
    public Vehicle findByVin(String vin) throws Exception {
        String sql = "SELECT ID, OwnerID, VIN, Reg, Brand, Model, YearManufact, HorsePower, CategoryLic, CreatedAt, IsActive " +
                     "FROM Vehicle WHERE VIN = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vin);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Vehicle findByVinAndOwner(String vin, int ownerId) throws Exception {
        String sql = "SELECT ID, OwnerID, VIN, Reg, Brand, Model, YearManufact, HorsePower, CategoryLic, CreatedAt, IsActive " +
                     "FROM Vehicle WHERE VIN = ? AND OwnerID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, vin);
            ps.setInt(2, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public Vehicle findByRegAndOwner(String reg, int ownerId) throws Exception {
        String sql = "SELECT ID, OwnerID, VIN, Reg, Brand, Model, YearManufact, HorsePower, CategoryLic, CreatedAt, IsActive " +
                     "FROM Vehicle WHERE Reg = ? AND OwnerID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reg);
            ps.setInt(2, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void create(Vehicle vehicle) throws Exception {
        String sql = "INSERT INTO Vehicle (OwnerID, VIN, Reg, Brand, Model, YearManufact, HorsePower, CategoryLic, CreatedAt, IsActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, vehicle.getOwnerId());
            ps.setString(2, vehicle.getVin());
            ps.setString(3, vehicle.getReg());
            ps.setString(4, vehicle.getBrand());
            ps.setString(5, vehicle.getModel());
            ps.setInt(6, vehicle.getYearManufact());
            ps.setInt(7, vehicle.getHorsePower());
            ps.setString(8, vehicle.getCategoryLic());
            ps.setTimestamp(9, Timestamp.valueOf(vehicle.getCreatedAt()));
            ps.setBoolean(10, vehicle.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    vehicle.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Vehicle vehicle) throws Exception {
        String sql = "UPDATE Vehicle SET OwnerID=?, VIN=?, Reg=?, Brand=?, Model=?, YearManufact=?, HorsePower=?, CategoryLic=?, IsActive=? WHERE ID=?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicle.getOwnerId());
            ps.setString(2, vehicle.getVin());
            ps.setString(3, vehicle.getReg());
            ps.setString(4, vehicle.getBrand());
            ps.setString(5, vehicle.getModel());
            ps.setInt(6, vehicle.getYearManufact());
            ps.setInt(7, vehicle.getHorsePower());
            ps.setString(8, vehicle.getCategoryLic());
            ps.setBoolean(9, vehicle.isActive());
            ps.setInt(10, vehicle.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "UPDATE Vehicle SET IsActive = 0 WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Vehicle mapRow(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getInt("ID"));
        vehicle.setOwnerId(rs.getInt("OwnerID"));
        vehicle.setVin(rs.getString("VIN"));
        vehicle.setReg(rs.getString("Reg"));
        vehicle.setBrand(rs.getString("Brand"));
        vehicle.setModel(rs.getString("Model"));
        vehicle.setYearManufact(rs.getInt("YearManufact"));
        vehicle.setHorsePower(rs.getInt("HorsePower"));
        vehicle.setCategoryLic(rs.getString("CategoryLic"));
        Timestamp created = rs.getTimestamp("CreatedAt");
        if (created != null) {
            vehicle.setCreatedAt(created.toLocalDateTime());
        }
        vehicle.setActive(rs.getBoolean("IsActive"));
        return vehicle;
    }
}

