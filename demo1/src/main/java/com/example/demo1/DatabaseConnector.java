package com.example.demo1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            // Replace with your actual DB credentials
            String url = "jdbc:mysql://localhost:3306/vehiclerentals";
            String user = "root";
            String password = "123456";

            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    public static void closeAllConnections() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
