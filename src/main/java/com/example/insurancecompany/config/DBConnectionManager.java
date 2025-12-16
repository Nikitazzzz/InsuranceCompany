package com.example.insurancecompany.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionManager {

    private static String url;
    private static String user;
    private static String password;

    public static void init(String urlParam, String userParam, String passwordParam) {
        url = urlParam;
        user = userParam;
        password = passwordParam;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}