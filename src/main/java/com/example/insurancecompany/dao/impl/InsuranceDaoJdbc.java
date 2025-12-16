package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.InsuranceDao;
import com.example.insurancecompany.model.InsuranceCase;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InsuranceDaoJdbc implements InsuranceDao {

    @Override
    public InsuranceCase findById(int id) throws Exception {
        String sql = "SELECT ID, PolicyID, OwnerID, StatID, IncidentDate, IncidentDescription, DescriptionDamage, GradeDamage, AdminComment, CreateDate, DecisionDate, IsActive " +
                     "FROM Insurances WHERE ID = ?";
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
    public List<InsuranceCase> findByOwnerId(int ownerId) throws Exception {
        List<InsuranceCase> cases = new ArrayList<>();
        String sql = "SELECT ID, PolicyID, OwnerID, StatID, IncidentDate, IncidentDescription, DescriptionDamage, GradeDamage, AdminComment, CreateDate, DecisionDate, IsActive " +
                     "FROM Insurances WHERE OwnerID = ? AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cases.add(mapRow(rs));
                }
            }
        }
        return cases;
    }

    @Override
    public List<InsuranceCase> findByPolicyId(int policyId) throws Exception {
        List<InsuranceCase> cases = new ArrayList<>();
        String sql = "SELECT ID, PolicyID, OwnerID, StatID, IncidentDate, IncidentDescription, DescriptionDamage, GradeDamage, AdminComment, CreateDate, DecisionDate, IsActive " +
                     "FROM Insurances WHERE PolicyID = ? AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, policyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cases.add(mapRow(rs));
                }
            }
        }
        return cases;
    }

    @Override
    public List<InsuranceCase> findByStatusId(int statusId) throws Exception {
        List<InsuranceCase> cases = new ArrayList<>();
        String sql = "SELECT ID, PolicyID, OwnerID, StatID, IncidentDate, IncidentDescription, DescriptionDamage, GradeDamage, AdminComment, CreateDate, DecisionDate, IsActive " +
                     "FROM Insurances WHERE StatID = ? AND IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    cases.add(mapRow(rs));
                }
            }
        }
        return cases;
    }

    @Override
    public List<InsuranceCase> findAll() throws Exception {
        List<InsuranceCase> cases = new ArrayList<>();
        String sql = "SELECT ID, PolicyID, OwnerID, StatID, IncidentDate, IncidentDescription, DescriptionDamage, GradeDamage, AdminComment, CreateDate, DecisionDate, IsActive " +
                     "FROM Insurances WHERE IsActive = 1";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                cases.add(mapRow(rs));
            }
        }
        return cases;
    }

    @Override
    public void create(InsuranceCase insuranceCase) throws Exception {
        String sql = "INSERT INTO Insurances (PolicyID, OwnerID, StatID, IncidentDate, IncidentDescription, DescriptionDamage, GradeDamage, AdminComment, CreateDate, DecisionDate, IsActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, insuranceCase.getPolicyId());
            ps.setInt(2, insuranceCase.getOwnerId());
            ps.setInt(3, insuranceCase.getStatId());
            ps.setTimestamp(4, Timestamp.valueOf(insuranceCase.getIncidentDate()));
            ps.setString(5, insuranceCase.getIncidentDescription());
            ps.setString(6, insuranceCase.getDescriptionDamage());
            ps.setBigDecimal(7, insuranceCase.getGradeDamage());
            String adminComment = insuranceCase.getAdminComment();
            if (adminComment != null && !adminComment.isEmpty()) {
                ps.setString(8, adminComment);
            } else {
                ps.setNull(8, Types.VARCHAR);
            }
            ps.setTimestamp(9, Timestamp.valueOf(insuranceCase.getCreateDate()));
            if (insuranceCase.getDecisionDate() != null) {
                ps.setTimestamp(10, Timestamp.valueOf(insuranceCase.getDecisionDate()));
            } else {
                ps.setNull(10, Types.TIMESTAMP);
            }
            ps.setBoolean(11, insuranceCase.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    insuranceCase.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(InsuranceCase insuranceCase) throws Exception {
        String sql = "UPDATE Insurances SET PolicyID=?, OwnerID=?, StatID=?, IncidentDate=?, IncidentDescription=?, DescriptionDamage=?, GradeDamage=?, AdminComment=?, DecisionDate=?, IsActive=? WHERE ID=?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, insuranceCase.getPolicyId());
            ps.setInt(2, insuranceCase.getOwnerId());
            ps.setInt(3, insuranceCase.getStatId());
            ps.setTimestamp(4, Timestamp.valueOf(insuranceCase.getIncidentDate()));
            ps.setString(5, insuranceCase.getIncidentDescription());
            ps.setString(6, insuranceCase.getDescriptionDamage());
            ps.setBigDecimal(7, insuranceCase.getGradeDamage());
            String adminComment = insuranceCase.getAdminComment();
            if (adminComment != null && !adminComment.isEmpty()) {
                ps.setString(8, adminComment);
            } else {
                ps.setNull(8, Types.VARCHAR);
            }
            if (insuranceCase.getDecisionDate() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(insuranceCase.getDecisionDate()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }
            ps.setBoolean(10, insuranceCase.isActive());
            ps.setInt(11, insuranceCase.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "UPDATE Insurances SET IsActive = 0 WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private InsuranceCase mapRow(ResultSet rs) throws SQLException {
        InsuranceCase insuranceCase = new InsuranceCase();
        insuranceCase.setId(rs.getInt("ID"));
        insuranceCase.setPolicyId(rs.getInt("PolicyID"));
        insuranceCase.setOwnerId(rs.getInt("OwnerID"));
        insuranceCase.setStatId(rs.getInt("StatID"));
        Timestamp incidentDate = rs.getTimestamp("IncidentDate");
        if (incidentDate != null) {
            insuranceCase.setIncidentDate(incidentDate.toLocalDateTime());
        }
        insuranceCase.setIncidentDescription(rs.getString("IncidentDescription"));
        insuranceCase.setDescriptionDamage(rs.getString("DescriptionDamage"));
        BigDecimal gradeDamage = rs.getBigDecimal("GradeDamage");
        if (gradeDamage != null) {
            insuranceCase.setGradeDamage(gradeDamage);
        }
        String adminComment = rs.getString("AdminComment");
        insuranceCase.setAdminComment(adminComment);
        Timestamp createDate = rs.getTimestamp("CreateDate");
        if (createDate != null) {
            insuranceCase.setCreateDate(createDate.toLocalDateTime());
        }
        Timestamp decisionDate = rs.getTimestamp("DecisionDate");
        if (decisionDate != null) {
            insuranceCase.setDecisionDate(decisionDate.toLocalDateTime());
        }
        insuranceCase.setActive(rs.getBoolean("IsActive"));
        return insuranceCase;
    }
}

