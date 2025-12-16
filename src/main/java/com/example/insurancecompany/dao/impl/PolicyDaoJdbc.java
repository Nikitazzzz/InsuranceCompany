package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.PolicyDao;
import com.example.insurancecompany.model.Policy;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PolicyDaoJdbc implements PolicyDao {

    @Override
    public Policy findById(int id) throws Exception {
        String sql = "SELECT ID, OwnerID, VehicleID, TariffID, StatID, PolicyNumber, StartDate, EndDate, Price, CreatedAt, IsActive " +
                     "FROM Policies WHERE ID = ?";
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
    public Policy findByPolicyNumber(String policyNumber) throws Exception {
        String sql = "SELECT ID, OwnerID, VehicleID, TariffID, StatID, PolicyNumber, StartDate, EndDate, Price, CreatedAt, IsActive " +
                     "FROM Policies WHERE PolicyNumber = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, policyNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Policy> findByOwnerId(int ownerId) throws Exception {
        List<Policy> policies = new ArrayList<>();
        String sql = "SELECT ID, OwnerID, VehicleID, TariffID, StatID, PolicyNumber, StartDate, EndDate, Price, CreatedAt, IsActive " +
                     "FROM Policies WHERE OwnerID = ? AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    policies.add(mapRow(rs));
                }
            }
        }
        return policies;
    }

    @Override
    public List<Policy> findActiveByOwnerId(int ownerId) throws Exception {
        List<Policy> policies = new ArrayList<>();
        String sql = "SELECT ID, OwnerID, VehicleID, TariffID, StatID, PolicyNumber, StartDate, EndDate, Price, CreatedAt, IsActive " +
                     "FROM Policies WHERE OwnerID = ? AND StatID = 1 AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    policies.add(mapRow(rs));
                }
            }
        }
        return policies;
    }

    @Override
    public List<Policy> findByVehicleId(int vehicleId) throws Exception {
        List<Policy> policies = new ArrayList<>();
        String sql = "SELECT ID, OwnerID, VehicleID, TariffID, StatID, PolicyNumber, StartDate, EndDate, Price, CreatedAt, IsActive " +
                     "FROM Policies WHERE VehicleID = ? AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    policies.add(mapRow(rs));
                }
            }
        }
        return policies;
    }

    @Override
    public List<Policy> findByStatusId(int statusId) throws Exception {
        List<Policy> policies = new ArrayList<>();
        String sql = "SELECT ID, OwnerID, VehicleID, TariffID, StatID, PolicyNumber, StartDate, EndDate, Price, CreatedAt, IsActive " +
                     "FROM Policies WHERE StatID = ? AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    policies.add(mapRow(rs));
                }
            }
        }
        return policies;
    }

    @Override
    public List<Policy> findActive() throws Exception {
        List<Policy> policies = new ArrayList<>();
        String sql = "SELECT ID, OwnerID, VehicleID, TariffID, StatID, PolicyNumber, StartDate, EndDate, Price, CreatedAt, IsActive " +
                     "FROM Policies WHERE IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                policies.add(mapRow(rs));
            }
        }
        return policies;
    }

    @Override
    public List<Policy> findAll() throws Exception {
        List<Policy> policies = new ArrayList<>();
        String sql = "SELECT ID, OwnerID, VehicleID, TariffID, StatID, PolicyNumber, StartDate, EndDate, Price, CreatedAt, IsActive " +
                     "FROM Policies ORDER BY CreatedAt DESC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                policies.add(mapRow(rs));
            }
        }
        return policies;
    }

    @Override
    public void create(Policy policy) throws Exception {
        String sql = "INSERT INTO Policies (OwnerID, VehicleID, TariffID, StatID, PolicyNumber, StartDate, EndDate, Price, CreatedAt, IsActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, policy.getOwnerId());
            ps.setInt(2, policy.getVehicleId());
            ps.setInt(3, policy.getTariffId());
            ps.setInt(4, policy.getStatId());
            ps.setString(5, policy.getPolicyNumber());
            ps.setDate(6, Date.valueOf(policy.getStartDate()));
            ps.setDate(7, Date.valueOf(policy.getEndDate()));
            ps.setBigDecimal(8, policy.getPrice());
            ps.setTimestamp(9, Timestamp.valueOf(policy.getCreatedAt()));
            ps.setBoolean(10, policy.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    policy.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Policy policy) throws Exception {
        String sql = "UPDATE Policies SET OwnerID=?, VehicleID=?, TariffID=?, StatID=?, PolicyNumber=?, StartDate=?, EndDate=?, Price=?, IsActive=? WHERE ID=?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, policy.getOwnerId());
            ps.setInt(2, policy.getVehicleId());
            ps.setInt(3, policy.getTariffId());
            ps.setInt(4, policy.getStatId());
            ps.setString(5, policy.getPolicyNumber());
            ps.setDate(6, Date.valueOf(policy.getStartDate()));
            ps.setDate(7, Date.valueOf(policy.getEndDate()));
            ps.setBigDecimal(8, policy.getPrice());
            ps.setBoolean(9, policy.isActive());
            ps.setInt(10, policy.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "UPDATE Policies SET IsActive = 0 WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Policy mapRow(ResultSet rs) throws SQLException {
        Policy policy = new Policy();
        policy.setId(rs.getInt("ID"));
        policy.setOwnerId(rs.getInt("OwnerID"));
        policy.setVehicleId(rs.getInt("VehicleID"));
        policy.setTariffId(rs.getInt("TariffID"));
        policy.setStatId(rs.getInt("StatID"));
        policy.setPolicyNumber(rs.getString("PolicyNumber"));
        Date startDate = rs.getDate("StartDate");
        if (startDate != null) {
            policy.setStartDate(startDate.toLocalDate());
        }
        Date endDate = rs.getDate("EndDate");
        if (endDate != null) {
            policy.setEndDate(endDate.toLocalDate());
        }
        policy.setPrice(rs.getBigDecimal("Price"));
        Timestamp created = rs.getTimestamp("CreatedAt");
        if (created != null) {
            policy.setCreatedAt(created.toLocalDateTime());
        }
        policy.setActive(rs.getBoolean("IsActive"));
        return policy;
    }
}

