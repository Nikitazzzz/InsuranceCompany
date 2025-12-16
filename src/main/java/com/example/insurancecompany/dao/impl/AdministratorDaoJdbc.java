package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.AdministratorDao;
import com.example.insurancecompany.model.Administrator;

import java.sql.*;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AdministratorDaoJdbc implements AdministratorDao {

    @Override
    public Administrator findById(int id) throws Exception {
        String sql = "SELECT ID, AName, Surname, Position, WorkEXP, Email, IsActive, CreatedAt " +
                     "FROM Administrator WHERE ID = ?";
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
    public Administrator findByEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT ID, AName, Surname, Position, WorkEXP, Email, IsActive, CreatedAt " +
                     "FROM Administrator WHERE Email = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Administrator> findAll() throws Exception {
        List<Administrator> administrators = new ArrayList<>();
        String sql = "SELECT ID, AName, Surname, Position, WorkEXP, Email, IsActive, CreatedAt " +
                     "FROM Administrator";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                administrators.add(mapRow(rs));
            }
        }
        return administrators;
    }

    @Override
    public void create(Administrator administrator) throws Exception {
        String sql = "INSERT INTO Administrator (AName, Surname, Position, WorkEXP, Email, IsActive, CreatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, administrator.getName());
            ps.setString(2, administrator.getSurname());
            ps.setString(3, administrator.getPosition());
            ps.setInt(4, administrator.getWorkExp());
            // Если email пустой или null, устанавливаем NULL
            String email = administrator.getEmail();
            if (email != null && email.trim().isEmpty()) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, email);
            }
            ps.setBoolean(6, administrator.isActive());
            ps.setTimestamp(7, Timestamp.valueOf(administrator.getCreatedAt()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    administrator.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Administrator administrator) throws Exception {
        String sql = "UPDATE Administrator SET AName=?, Surname=?, Position=?, WorkEXP=?, Email=?, IsActive=? WHERE ID=?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, administrator.getName());
            ps.setString(2, administrator.getSurname());
            ps.setString(3, administrator.getPosition());
            ps.setInt(4, administrator.getWorkExp());
            // Если email пустой или null, устанавливаем NULL
            String email = administrator.getEmail();
            if (email != null && email.trim().isEmpty()) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, email);
            }
            ps.setBoolean(6, administrator.isActive());
            ps.setInt(7, administrator.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "UPDATE Administrator SET IsActive = 0 WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Administrator mapRow(ResultSet rs) throws SQLException {
        Administrator admin = new Administrator();
        admin.setId(rs.getInt("ID"));
        admin.setName(rs.getString("AName"));
        admin.setSurname(rs.getString("Surname"));
        admin.setPosition(rs.getString("Position"));
        admin.setWorkExp(rs.getInt("WorkEXP"));
        admin.setEmail(rs.getString("Email"));
        admin.setActive(rs.getBoolean("IsActive"));
        Timestamp created = rs.getTimestamp("CreatedAt");
        if (created != null) {
            admin.setCreatedAt(created.toLocalDateTime());
        }
        return admin;
    }
}

