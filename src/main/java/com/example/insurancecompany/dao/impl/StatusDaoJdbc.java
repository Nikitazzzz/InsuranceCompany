package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.StatusDao;
import com.example.insurancecompany.model.Status;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatusDaoJdbc implements StatusDao {

    @Override
    public Status findById(int id) throws Exception {
        String sql = "SELECT ID, StName FROM Statuses WHERE ID = ?";
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
    public Status findByName(String name) throws Exception {
        String sql = "SELECT ID, StName FROM Statuses WHERE StName = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Status> findAll() throws Exception {
        List<Status> statuses = new ArrayList<>();
        String sql = "SELECT ID, StName FROM Statuses";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                statuses.add(mapRow(rs));
            }
        }
        return statuses;
    }

    private Status mapRow(ResultSet rs) throws SQLException {
        Status status = new Status();
        status.setId(rs.getInt("ID"));
        status.setStName(rs.getString("StName"));
        return status;
    }
}



