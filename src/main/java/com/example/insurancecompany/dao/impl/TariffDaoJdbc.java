package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.TariffDao;
import com.example.insurancecompany.model.Tariff;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TariffDaoJdbc implements TariffDao {

    @Override
    public Tariff findById(int id) throws Exception {
        String sql = "SELECT ID, StatID, TariffName, PolicyType, BasePrice, RegionCoefficient, DriverEXPCoefficient, PowerCoefficient, Description, IsActive, CreatedAt " +
                     "FROM Tariffs WHERE ID = ?";
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
    public List<Tariff> findAll() throws Exception {
        List<Tariff> tariffs = new ArrayList<>();
        String sql = "SELECT ID, StatID, TariffName, PolicyType, BasePrice, RegionCoefficient, DriverEXPCoefficient, PowerCoefficient, Description, IsActive, CreatedAt " +
                     "FROM Tariffs";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tariffs.add(mapRow(rs));
            }
        }
        return tariffs;
    }

    @Override
    public List<Tariff> findByPolicyType(String policyType) throws Exception {
        List<Tariff> tariffs = new ArrayList<>();
        String sql = "SELECT ID, StatID, TariffName, PolicyType, BasePrice, RegionCoefficient, DriverEXPCoefficient, PowerCoefficient, Description, IsActive, CreatedAt " +
                     "FROM Tariffs WHERE PolicyType = ? AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, policyType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    tariffs.add(mapRow(rs));
                }
            }
        }
        return tariffs;
    }

    @Override
    public List<Tariff> findActive() throws Exception {
        List<Tariff> tariffs = new ArrayList<>();
        String sql = "SELECT ID, StatID, TariffName, PolicyType, BasePrice, RegionCoefficient, DriverEXPCoefficient, PowerCoefficient, Description, IsActive, CreatedAt " +
                     "FROM Tariffs WHERE IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tariffs.add(mapRow(rs));
            }
        }
        return tariffs;
    }

    @Override
    public void create(Tariff tariff) throws Exception {
        String sql = "INSERT INTO Tariffs (StatID, TariffName, PolicyType, BasePrice, RegionCoefficient, DriverEXPCoefficient, PowerCoefficient, Description, IsActive, CreatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tariff.getStatId());
            ps.setString(2, tariff.getTariffName());
            ps.setString(3, tariff.getPolicyType());
            ps.setBigDecimal(4, tariff.getBasePrice());
            ps.setBigDecimal(5, tariff.getRegionCoefficient());
            ps.setBigDecimal(6, tariff.getDriverExpCoefficient());
            ps.setBigDecimal(7, tariff.getPowerCoefficient());
            ps.setString(8, tariff.getDescription());
            ps.setBoolean(9, tariff.isActive());
            ps.setTimestamp(10, Timestamp.valueOf(tariff.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    tariff.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Tariff tariff) throws Exception {
        String sql = "UPDATE Tariffs SET StatID=?, TariffName=?, PolicyType=?, BasePrice=?, RegionCoefficient=?, DriverEXPCoefficient=?, PowerCoefficient=?, Description=?, IsActive=? WHERE ID=?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tariff.getStatId());
            ps.setString(2, tariff.getTariffName());
            ps.setString(3, tariff.getPolicyType());
            ps.setBigDecimal(4, tariff.getBasePrice());
            ps.setBigDecimal(5, tariff.getRegionCoefficient());
            ps.setBigDecimal(6, tariff.getDriverExpCoefficient());
            ps.setBigDecimal(7, tariff.getPowerCoefficient());
            ps.setString(8, tariff.getDescription());
            ps.setBoolean(9, tariff.isActive());
            ps.setInt(10, tariff.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "UPDATE Tariffs SET IsActive = 0 WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Tariff mapRow(ResultSet rs) throws SQLException {
        Tariff tariff = new Tariff();
        tariff.setId(rs.getInt("ID"));
        tariff.setStatId(rs.getInt("StatID"));
        tariff.setTariffName(rs.getString("TariffName"));
        tariff.setPolicyType(rs.getString("PolicyType"));
        tariff.setBasePrice(rs.getBigDecimal("BasePrice"));
        tariff.setRegionCoefficient(rs.getBigDecimal("RegionCoefficient"));
        tariff.setDriverExpCoefficient(rs.getBigDecimal("DriverEXPCoefficient"));
        tariff.setPowerCoefficient(rs.getBigDecimal("PowerCoefficient"));
        tariff.setDescription(rs.getString("Description"));
        tariff.setActive(rs.getBoolean("IsActive"));
        Timestamp created = rs.getTimestamp("CreatedAt");
        if (created != null) {
            tariff.setCreatedAt(created.toLocalDateTime());
        }
        return tariff;
    }
}

