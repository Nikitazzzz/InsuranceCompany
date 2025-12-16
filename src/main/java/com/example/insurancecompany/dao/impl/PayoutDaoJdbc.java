package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.PayoutDao;
import com.example.insurancecompany.model.Payout;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PayoutDaoJdbc implements PayoutDao {

    @Override
    public Payout findById(int id) throws Exception {
        String sql = "SELECT ID, InsuranceID, AdminID, SumPayout, PayoutDate, PaymentMethod, IsActive " +
                     "FROM Payouts WHERE ID = ?";
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
    public Payout findByInsuranceId(int insuranceId) throws Exception {
        String sql = "SELECT ID, InsuranceID, AdminID, SumPayout, PayoutDate, PaymentMethod, IsActive " +
                     "FROM Payouts WHERE InsuranceID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, insuranceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Payout> findAll() throws Exception {
        List<Payout> payouts = new ArrayList<>();
        String sql = "SELECT ID, InsuranceID, AdminID, SumPayout, PayoutDate, PaymentMethod, IsActive " +
                     "FROM Payouts WHERE IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                payouts.add(mapRow(rs));
            }
        }
        return payouts;
    }

    @Override
    public List<Payout> findByAdminId(int adminId) throws Exception {
        List<Payout> payouts = new ArrayList<>();
        String sql = "SELECT ID, InsuranceID, AdminID, SumPayout, PayoutDate, PaymentMethod, IsActive " +
                     "FROM Payouts WHERE AdminID = ? AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    payouts.add(mapRow(rs));
                }
            }
        }
        return payouts;
    }

    @Override
    public void create(Payout payout) throws Exception {
        String sql = "INSERT INTO Payouts (InsuranceID, AdminID, SumPayout, PayoutDate, PaymentMethod, IsActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payout.getInsuranceId());
            ps.setInt(2, payout.getAdminId());
            ps.setBigDecimal(3, payout.getSumPayout());
            ps.setTimestamp(4, Timestamp.valueOf(payout.getPayoutDate()));
            ps.setString(5, payout.getPaymentMethod());
            ps.setBoolean(6, payout.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    payout.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Payout payout) throws Exception {
        String sql = "UPDATE Payouts SET InsuranceID=?, AdminID=?, SumPayout=?, PayoutDate=?, PaymentMethod=?, IsActive=? WHERE ID=?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payout.getInsuranceId());
            ps.setInt(2, payout.getAdminId());
            ps.setBigDecimal(3, payout.getSumPayout());
            ps.setTimestamp(4, Timestamp.valueOf(payout.getPayoutDate()));
            ps.setString(5, payout.getPaymentMethod());
            ps.setBoolean(6, payout.isActive());
            ps.setInt(7, payout.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "UPDATE Payouts SET IsActive = 0 WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Payout mapRow(ResultSet rs) throws SQLException {
        Payout payout = new Payout();
        payout.setId(rs.getInt("ID"));
        payout.setInsuranceId(rs.getInt("InsuranceID"));
        payout.setAdminId(rs.getInt("AdminID"));
        payout.setSumPayout(rs.getBigDecimal("SumPayout"));
        Timestamp payoutDate = rs.getTimestamp("PayoutDate");
        if (payoutDate != null) {
            payout.setPayoutDate(payoutDate.toLocalDateTime());
        }
        payout.setPaymentMethod(rs.getString("PaymentMethod"));
        payout.setActive(rs.getBoolean("IsActive"));
        return payout;
    }
}

