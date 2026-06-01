package com.fooddelivery.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/fooddelivery";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        connect();
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Failed to check connection state: " + e.getMessage());
            connect();
        }
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DatabaseConnection] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DatabaseConnection] Failed to close connection: " + e.getMessage());
            }
        }
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DatabaseConnection] Connected to PostgreSQL.");
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Connection failed: " + e.getMessage());
            connection = null;
        }
    }
}
