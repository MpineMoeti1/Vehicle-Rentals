package com.example.demo1;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Booking {
    private int bookingId;  // Unique identifier from the database
    private int customerId;
    private int vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private boolean isActive;

    public Booking(int bookingId, int customerId, int vehicleId, LocalDate startDate, LocalDate endDate, BigDecimal totalPrice, boolean isActive) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalPrice = totalPrice;
        this.isActive = isActive;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", customerId=" + customerId +
                ", vehicleId=" + vehicleId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", totalPrice=" + totalPrice +
                ", isActive=" + isActive +
                '}';
    }

    // Method to fetch a booking based on booking ID
    public static Booking getBookingById(String bookingIdStr) {
        Booking booking = null;
        int bookingId;

        try {
            bookingId = Integer.parseInt(bookingIdStr); // Attempt to parse booking ID
        } catch (NumberFormatException e) {
            return null; // Return null if IDs cannot be parsed
        }

        // SQL query to fetch booking information
        String sql = "SELECT * FROM bookings WHERE booking_id = ? AND is_active = 1"; // Modify as necessary
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/vehiclerentals", "root", "123456");
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                // Populate the Booking object with data from the ResultSet
                booking = new Booking(
                        resultSet.getInt("booking_id"),
                        resultSet.getInt("customer_id"),
                        resultSet.getInt("vehicle_id"),
                        resultSet.getDate("start_date") != null ? resultSet.getDate("start_date").toLocalDate() : null,
                        resultSet.getDate("end_date") != null ? resultSet.getDate("end_date").toLocalDate() : null,
                        resultSet.getBigDecimal("total_price"),
                        resultSet.getBoolean("is_active")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print exception trace for debugging
        }
        return booking; // Return fetched booking or null if not found
    }
}