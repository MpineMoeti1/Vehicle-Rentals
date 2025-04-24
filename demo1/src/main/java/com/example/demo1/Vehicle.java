package com.example.demo1;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Vehicle {
    private String id;
    private String brand;
    private String model;
    private String category;
    private double pricePerDay;
    private boolean available;

    // Constructor
    public Vehicle(String id, String brand, String model, String category,
                   double pricePerDay, boolean available) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.category = category;
        this.pricePerDay = pricePerDay;
        this.available = available;
    }

    // Getters
    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public String getCategory() { return category; }
    public double getPricePerDay() { return pricePerDay; }
    public boolean isAvailable() { return available; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setCategory(String category) { this.category = category; }
    public void setPricePerDay(double pricePerDay) { this.pricePerDay = pricePerDay; }
    public void setAvailable(boolean available) { this.available = available; }

    // Database operations for Vehicle
    public static boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (vehicle_id, brand, model, category, rental_price, availability_status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getId());
            pstmt.setString(2, vehicle.getBrand());
            pstmt.setString(3, vehicle.getModel());
            pstmt.setString(4, vehicle.getCategory());
            pstmt.setDouble(5, vehicle.getPricePerDay());
            pstmt.setInt(6, vehicle.isAvailable() ? 1 : 0); // 1 for available, 0 for not
            return pstmt.executeUpdate() > 0; // returns true if a row was inserted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET brand = ?, model = ?, category = ?, rental_price = ?, availability_status = ? WHERE vehicle_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicle.getBrand());
            pstmt.setString(2, vehicle.getModel());
            pstmt.setString(3, vehicle.getCategory());
            pstmt.setDouble(4, vehicle.getPricePerDay());
            pstmt.setInt(5, vehicle.isAvailable() ? 1 : 0);
            pstmt.setString(6, vehicle.getId());
            return pstmt.executeUpdate() > 0; // updates the vehicle
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteVehicle(String vehicleId) {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicleId);
            return pstmt.executeUpdate() > 0; // deletes the vehicle
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vehicles.add(new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("category"),
                        rs.getDouble("rental_price"),
                        rs.getInt("availability_status") == 1 // Assumes 1 is available
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles; // Returns a list of all vehicles
    }

    public static Vehicle getVehicleById(String vehicleId) {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vehicleId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("category"),
                        rs.getDouble("rental_price"),
                        rs.getInt("availability_status") == 1
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // returns null if no vehicle is found
    }

    public static List<Vehicle> searchVehicles(String searchTerm) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE vehicle_id LIKE ? OR brand LIKE ? OR model LIKE ? OR category LIKE ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%"; // Wildcard pattern for search
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);
            pstmt.setString(4, likeTerm);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                vehicles.add(new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("category"),
                        rs.getDouble("rental_price"),
                        rs.getInt("availability_status") == 1
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles; // Returns a list of vehicles matching the search term
    }

    // New method to get counts of available and rented vehicles
    public static int getAvailableVehicleCount() {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE availability_status = 1";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1); // returns the count of available vehicles
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if there was an error
    }

    public static int getRentedVehicleCount() {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE availability_status = 0";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1); // returns the count of rented vehicles
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Return 0 if there was an error
    }
    // New method to get available vehicles by category
    public static Map<String, Integer> getAvailableVehiclesByCategory() {
        Map<String, Integer> categoryCounts = new HashMap<>();
        String sql = "SELECT category, COUNT(*) AS count FROM vehicles WHERE availability_status = 1 GROUP BY category";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category");
                int count = rs.getInt("count");
                categoryCounts.put(category, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoryCounts;
    }


}