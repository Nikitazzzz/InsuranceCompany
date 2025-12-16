package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.OwnerDao;
import com.example.insurancecompany.model.Owner;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OwnerDaoJdbc implements OwnerDao {

    @Override
    public Owner findById(int id) throws Exception {
        String sql = "SELECT ID, Email, OName, Surname, MiddleName, Phone, Birthday, DriverEXP, CreatedAt, IsActive " +
                     "FROM Owners WHERE ID = ?";
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
    public Owner findByEmail(String email) throws Exception {
        String sql = "SELECT ID, Email, OName, Surname, MiddleName, Phone, Birthday, DriverEXP, CreatedAt, IsActive " +
                     "FROM Owners WHERE Email = ?";
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
    public List<Owner> findAll() throws Exception {
        List<Owner> owners = new ArrayList<>();
        String sql = "SELECT ID, Email, OName, Surname, MiddleName, Phone, Birthday, DriverEXP, CreatedAt, IsActive " +
                     "FROM Owners";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                owners.add(mapRow(rs));
            }
        }
        return owners;
    }

    @Override
    public List<Owner> findByUserId(int userId) throws Exception {
        List<Owner> owners = new ArrayList<>();
        String sql = "SELECT o.ID, o.Email, o.OName, o.Surname, o.MiddleName, o.Phone, o.Birthday, o.DriverEXP, o.CreatedAt, o.IsActive " +
                     "FROM Owners o INNER JOIN Users u ON o.ID = u.OwnerID WHERE u.ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    owners.add(mapRow(rs));
                }
            }
        }
        return owners;
    }

    @Override
    public void create(Owner owner) throws Exception {
        String sql = "INSERT INTO Owners (Email, OName, Surname, MiddleName, Phone, Birthday, DriverEXP, CreatedAt, IsActive) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, owner.getEmail());
            ps.setString(2, owner.getOName());
            ps.setString(3, owner.getSurname());
            ps.setString(4, owner.getMiddleName());
            ps.setString(5, owner.getPhone());
            ps.setDate(6, Date.valueOf(owner.getBirthday()));
            ps.setInt(7, owner.getDriverExp());
            ps.setTimestamp(8, Timestamp.valueOf(owner.getCreatedAt()));
            ps.setBoolean(9, owner.isActive());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    owner.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Owner owner) throws Exception {
        String sql = "UPDATE Owners SET Email=?, OName=?, Surname=?, MiddleName=?, Phone=?, Birthday=?, DriverEXP=?, IsActive=? WHERE ID=?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, owner.getEmail());
            ps.setString(2, owner.getOName());
            ps.setString(3, owner.getSurname());
            ps.setString(4, owner.getMiddleName());
            ps.setString(5, owner.getPhone());
            ps.setDate(6, Date.valueOf(owner.getBirthday()));
            ps.setInt(7, owner.getDriverExp());
            ps.setBoolean(8, owner.isActive());
            ps.setInt(9, owner.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "UPDATE Owners SET IsActive = 0 WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private Owner mapRow(ResultSet rs) throws SQLException {
        Owner owner = new Owner();
        owner.setId(rs.getInt("ID"));
        owner.setEmail(rs.getString("Email"));
        owner.setOName(rs.getString("OName"));
        owner.setSurname(rs.getString("Surname"));
        owner.setMiddleName(rs.getString("MiddleName"));
        Date birthday = rs.getDate("Birthday");
        if (birthday != null) {
            owner.setBirthday(birthday.toLocalDate());
        }
        owner.setPhone(rs.getString("Phone"));
        owner.setDriverExp(rs.getInt("DriverEXP"));
        Timestamp created = rs.getTimestamp("CreatedAt");
        if (created != null) {
            owner.setCreatedAt(created.toLocalDateTime());
        }
        owner.setActive(rs.getBoolean("IsActive"));
        return owner;
    }
}

