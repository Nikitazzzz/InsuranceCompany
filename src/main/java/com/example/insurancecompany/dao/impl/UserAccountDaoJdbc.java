package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.UserAccountDao;
import com.example.insurancecompany.model.UserAccount;

import java.sql.*;
import java.time.LocalDateTime;

public class UserAccountDaoJdbc implements UserAccountDao {

    @Override
    public UserAccount findByLogin(String login) throws Exception {
        String sql = "SELECT ID, Login, PasswordHash, Role, OwnerID, AdminID, IsActive, CreatedAt " +
                     "FROM Users WHERE Login = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public UserAccount findById(int id) throws Exception {
        String sql = "SELECT ID, Login, PasswordHash, Role, OwnerID, AdminID, IsActive, CreatedAt " +
                     "FROM Users WHERE ID = ?";
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
    public void create(UserAccount user) throws Exception {
        String sql = "INSERT INTO Users (Login, PasswordHash, Role, OwnerID, AdminID, IsActive, CreatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole());
            if (user.getOwnerId() != null) {
                ps.setInt(4, user.getOwnerId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            if (user.getAdminId() != null) {
                ps.setInt(5, user.getAdminId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setBoolean(6, user.isActive());
            ps.setTimestamp(7, Timestamp.valueOf(user.getCreatedAt()));

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(UserAccount user) throws Exception {
        String sql = "UPDATE Users SET Login=?, PasswordHash=?, Role=?, OwnerID=?, AdminID=?, IsActive=? WHERE ID=?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole());
            if (user.getOwnerId() != null) {
                ps.setInt(4, user.getOwnerId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            if (user.getAdminId() != null) {
                ps.setInt(5, user.getAdminId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setBoolean(6, user.isActive());
            ps.setInt(7, user.getId());

            ps.executeUpdate();
        }
    }

    private UserAccount mapRow(ResultSet rs) throws SQLException {
        UserAccount user = new UserAccount();
        user.setId(rs.getInt("ID"));
        user.setLogin(rs.getString("Login"));
        user.setPasswordHash(rs.getString("PasswordHash"));
        user.setRole(rs.getString("Role"));
        int ownerId = rs.getInt("OwnerID");
        if (!rs.wasNull()) {
            user.setOwnerId(ownerId);
        }
        int adminId = rs.getInt("AdminID");
        if (!rs.wasNull()) {
            user.setAdminId(adminId);
        }
        user.setActive(rs.getBoolean("IsActive"));
        Timestamp created = rs.getTimestamp("CreatedAt");
        if (created != null) {
            user.setCreatedAt(created.toLocalDateTime());
        } else {
            user.setCreatedAt(LocalDateTime.now());
        }
        return user;
    }
}

