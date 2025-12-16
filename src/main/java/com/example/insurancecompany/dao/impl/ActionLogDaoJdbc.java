package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.ActionLogDao;
import com.example.insurancecompany.model.ActionLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActionLogDaoJdbc implements ActionLogDao {

    @Override
    public void create(ActionLog actionLog) throws Exception {
        String sql = "INSERT INTO ActionLogs (UserID, UserRole, ActionType, EntityType, EntityID, Description, IPAddress, CreatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (actionLog.getUserId() != null) {
                ps.setInt(1, actionLog.getUserId());
            } else {
                ps.setNull(1, Types.INTEGER);
            }
            ps.setString(2, actionLog.getUserRole());
            ps.setString(3, actionLog.getActionType());
            ps.setString(4, actionLog.getEntityType());
            if (actionLog.getEntityId() != null) {
                ps.setInt(5, actionLog.getEntityId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setString(6, actionLog.getDescription());
            ps.setString(7, actionLog.getIpAddress());
            if (actionLog.getCreatedAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(actionLog.getCreatedAt()));
            } else {
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    actionLog.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<ActionLog> findAll() throws Exception {
        List<ActionLog> logs = new ArrayList<>();
        String sql = "SELECT ID, UserID, UserRole, ActionType, EntityType, EntityID, Description, IPAddress, CreatedAt " +
                     "FROM ActionLogs ORDER BY CreatedAt DESC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logs.add(mapRow(rs));
            }
        }
        return logs;
    }

    @Override
    public List<ActionLog> findByUserId(int userId) throws Exception {
        List<ActionLog> logs = new ArrayList<>();
        String sql = "SELECT ID, UserID, UserRole, ActionType, EntityType, EntityID, Description, IPAddress, CreatedAt " +
                     "FROM ActionLogs WHERE UserID = ? ORDER BY CreatedAt DESC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        }
        return logs;
    }

    @Override
    public List<ActionLog> findByActionType(String actionType) throws Exception {
        List<ActionLog> logs = new ArrayList<>();
        String sql = "SELECT ID, UserID, UserRole, ActionType, EntityType, EntityID, Description, IPAddress, CreatedAt " +
                     "FROM ActionLogs WHERE ActionType = ? ORDER BY CreatedAt DESC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, actionType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        }
        return logs;
    }

    @Override
    public List<ActionLog> findByEntityType(String entityType) throws Exception {
        List<ActionLog> logs = new ArrayList<>();
        String sql = "SELECT ID, UserID, UserRole, ActionType, EntityType, EntityID, Description, IPAddress, CreatedAt " +
                     "FROM ActionLogs WHERE EntityType = ? ORDER BY CreatedAt DESC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entityType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        }
        return logs;
    }

    @Override
    public List<ActionLog> findRecent(int limit) throws Exception {
        List<ActionLog> logs = new ArrayList<>();
        String sql = "SELECT ID, UserID, UserRole, ActionType, EntityType, EntityID, Description, IPAddress, CreatedAt " +
                     "FROM ActionLogs ORDER BY CreatedAt DESC LIMIT ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapRow(rs));
                }
            }
        }
        return logs;
    }

    private ActionLog mapRow(ResultSet rs) throws SQLException {
        ActionLog log = new ActionLog();
        log.setId(rs.getInt("ID"));
        int userId = rs.getInt("UserID");
        if (!rs.wasNull()) {
            log.setUserId(userId);
        }
        log.setUserRole(rs.getString("UserRole"));
        log.setActionType(rs.getString("ActionType"));
        log.setEntityType(rs.getString("EntityType"));
        int entityId = rs.getInt("EntityID");
        if (!rs.wasNull()) {
            log.setEntityId(entityId);
        }
        log.setDescription(rs.getString("Description"));
        log.setIpAddress(rs.getString("IPAddress"));
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            log.setCreatedAt(createdAt.toLocalDateTime());
        }
        return log;
    }
}

