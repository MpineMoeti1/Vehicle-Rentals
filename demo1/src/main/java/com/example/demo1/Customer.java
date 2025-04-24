package com.example.demo1;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Customer {
    private String customerId;
    private String name;
    private String contact;
    private String licenseNumber;
    private String rentalHistory;

    public Customer(String customerId, String name, String contact, String licenseNumber, String rentalHistory) {
        this.customerId = customerId;
        this.name = name;
        this.contact = contact;
        this.licenseNumber = licenseNumber;
        this.rentalHistory = rentalHistory;
    }

    // Getters
    public String getCustomerId() {
        return customerId;
    }

    public String getId() {  // Added for compatibility
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public String getRentalHistory() {
        return rentalHistory;
    }

    // Setters
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setRentalHistory(String rentalHistory) {
        this.rentalHistory = rentalHistory;
    }

    // Database operations
    public static boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (customer_id, name, contact, license_number, rental_history) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getContact());
            pstmt.setString(4, customer.getLicenseNumber());
            pstmt.setString(5, customer.getRentalHistory());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error adding customer: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET name = ?, contact = ?, license_number = ?, rental_history = ? WHERE customer_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getContact());
            pstmt.setString(3, customer.getLicenseNumber());
            pstmt.setString(4, customer.getRentalHistory());
            pstmt.setString(5, customer.getCustomerId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteCustomer(String customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    public static List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("license_number"),
                        rs.getString("rental_history")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("Error getting customers: " + e.getMessage());
        }
        return customers;
    }

    public static Customer getCustomerById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("license_number"),
                        rs.getString("rental_history")
                );
            }
        } catch (SQLException e) {
            System.out.println("Error getting customer by ID: " + e.getMessage());
        }
        return null;
    }

    public static List<Customer> searchCustomers(String searchTerm) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE customer_id LIKE ? OR name LIKE ? OR contact LIKE ? OR license_number LIKE ? OR rental_history LIKE ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);
            pstmt.setString(4, likeTerm);
            pstmt.setString(5, likeTerm);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getString("customer_id"),
                        rs.getString("name"),
                        rs.getString("contact"),
                        rs.getString("license_number"),
                        rs.getString("rental_history")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            System.out.println("Error searching customers: " + e.getMessage());
        }
        return customers;
    }
}
