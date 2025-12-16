package com.example.insurancecompany.dao.impl;

import com.example.insurancecompany.config.DBConnectionManager;
import com.example.insurancecompany.dao.InsurancePhotoDao;
import com.example.insurancecompany.model.InsurancePhoto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InsurancePhotoDaoJdbc implements InsurancePhotoDao {

    @Override
    public void create(InsurancePhoto photo) throws Exception {
        String sql = "INSERT INTO InsurancePhotos (InsuranceID, FileName, FilePath, FileSize, MimeType, CreatedAt) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, photo.getInsuranceId());
            ps.setString(2, photo.getFileName());
            ps.setString(3, photo.getFilePath());
            if (photo.getFileSize() != null) {
                ps.setLong(4, photo.getFileSize());
            } else {
                ps.setNull(4, Types.BIGINT);
            }
            ps.setString(5, photo.getMimeType());
            if (photo.getCreatedAt() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(photo.getCreatedAt()));
            } else {
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    photo.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public List<InsurancePhoto> findByInsuranceId(int insuranceId) throws Exception {
        List<InsurancePhoto> photos = new ArrayList<>();
        String sql = "SELECT ID, InsuranceID, FileName, FilePath, FileSize, MimeType, CreatedAt " +
                     "FROM InsurancePhotos WHERE InsuranceID = ? ORDER BY CreatedAt ASC";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, insuranceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    photos.add(mapRow(rs));
                }
            }
        }
        return photos;
    }

    @Override
    public InsurancePhoto findById(int id) throws Exception {
        String sql = "SELECT ID, InsuranceID, FileName, FilePath, FileSize, MimeType, CreatedAt " +
                     "FROM InsurancePhotos WHERE ID = ?";
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
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM InsurancePhotos WHERE ID = ?";
        try (Connection conn = DBConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private InsurancePhoto mapRow(ResultSet rs) throws SQLException {
        InsurancePhoto photo = new InsurancePhoto();
        photo.setId(rs.getInt("ID"));
        photo.setInsuranceId(rs.getInt("InsuranceID"));
        photo.setFileName(rs.getString("FileName"));
        photo.setFilePath(rs.getString("FilePath"));
        long fileSize = rs.getLong("FileSize");
        if (!rs.wasNull()) {
            photo.setFileSize(fileSize);
        }
        photo.setMimeType(rs.getString("MimeType"));
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        if (createdAt != null) {
            photo.setCreatedAt(createdAt.toLocalDateTime());
        }
        return photo;
    }
}

