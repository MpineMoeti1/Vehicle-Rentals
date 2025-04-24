package com.example.demo1;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Payment {
    private int paymentId;
    private int bookingId;
    private double amount;
    private String paymentMethod;
    private LocalDate paymentDate;
    private double additionalServices;
    private double lateFee;
    private String paymentStatus;
    private double rentalFee; // New field
    private int rentalDuration; // New field (duration in days)

    public Payment(int paymentId, int bookingId, double amount, String paymentMethod, LocalDate paymentDate,
                   double additionalServices, double lateFee, String paymentStatus, double rentalFee, int rentalDuration) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.additionalServices = additionalServices;
        this.lateFee = lateFee;
        this.paymentStatus = paymentStatus;
        this.rentalFee = rentalFee;
        this.rentalDuration = rentalDuration;
    }

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public double getAdditionalServices() {
        return additionalServices;
    }

    public double getLateFee() {
        return lateFee;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public double getRentalFee() {
        return rentalFee;
    }

    public int getRentalDuration() {
        return rentalDuration;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setAdditionalServices(double additionalServices) {
        this.additionalServices = additionalServices;
    }

    public void setLateFee(double lateFee) {
        this.lateFee = lateFee;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setRentalFee(double rentalFee) {
        this.rentalFee = rentalFee;
    }

    public void setRentalDuration(int rentalDuration) {
        this.rentalDuration = rentalDuration;
    }

    // Method to process payment
    public static boolean processPayment(Payment payment) {
        String sql = "INSERT INTO payments (booking_id, amount, payment_method, payment_date, " +
                "ADDITIONAL_SERVICES, LATE_FEE, RENTAL_FEE, RENTAL_DURATION, PAYMENT_STATUS) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, payment.getBookingId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setString(3, payment.getPaymentMethod());
            pstmt.setTimestamp(4, Timestamp.valueOf(payment.getPaymentDate().atStartOfDay()));
            pstmt.setDouble(5, payment.getAdditionalServices());
            pstmt.setDouble(6, payment.getLateFee());
            pstmt.setDouble(7, payment.getRentalFee());
            pstmt.setInt(8, payment.getRentalDuration());
            pstmt.setString(9, payment.getPaymentStatus()); // Now setting the payment status dynamically

            // Execute the insert operation
            int affectedRows = pstmt.executeUpdate();
            System.out.println("Executing SQL: " + pstmt.toString());

            if (affectedRows > 0) {
                // Retrieve the generated keys (payment ID)
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        payment.setPaymentId(generatedKeys.getInt(1));
                    }
                }
                return true; // Payment processed successfully
            }
            return false; // No rows affected
        } catch (SQLException e) {
            System.out.println("Error processing payment with booking ID " + payment.getBookingId() + ": " + e.getMessage());
            return false; // Indicate that the payment processing failed
        }
    }

    // Method to get payments by customer ID
    public static List<Payment> getPaymentsByCustomer(int customerId) {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, b.* FROM payments p " +
                "JOIN bookings b ON p.booking_id = b.id " +
                "WHERE b.customer_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment(
                        rs.getInt("p.payment_id"),
                        rs.getInt("p.booking_id"),
                        rs.getDouble("p.amount"),
                        rs.getString("p.payment_method"),
                        rs.getTimestamp("p.payment_date").toLocalDateTime().toLocalDate(),
                        rs.getDouble("p.ADDITIONAL_SERVICES"),
                        rs.getDouble("p.LATE_FEE"),
                        rs.getString("p.PAYMENT_STATUS"),
                        rs.getDouble("p.RENTAL_FEE"),
                        rs.getInt("p.RENTAL_DURATION")
                );

                payments.add(payment);
            }
        } catch (SQLException e) {
            System.out.println("Error getting customer payments: " + e.getMessage());
        }
        return payments;
    }

    // Method to get all payments
    public static List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT p.*, b.* FROM payments p " +
                "JOIN bookings b ON p.booking_id = b.id";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Payment payment = new Payment(
                        rs.getInt("p.payment_id"),
                        rs.getInt("p.booking_id"),
                        rs.getDouble("p.amount"),
                        rs.getString("p.payment_method"),
                        rs.getTimestamp("p.payment_date").toLocalDateTime().toLocalDate(),
                        rs.getDouble("p.ADDITIONAL_SERVICES"),
                        rs.getDouble("p.LATE_FEE"),
                        rs.getString("p.PAYMENT_STATUS"),
                        rs.getDouble("p.RENTAL_FEE"),
                        rs.getInt("p.RENTAL_DURATION")
                );

                payments.add(payment);
            }
        } catch (SQLException e) {
            System.out.println("Error getting all payments: " + e.getMessage());
        }
        return payments;
    }

    // Method to get total revenue
    public static double getTotalRevenue() {
        String sql = "SELECT SUM(amount) AS total FROM payments WHERE PAYMENT_STATUS = 'COMPLETED'";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.out.println("Error calculating total revenue: " + e.getMessage());
        }
        return 0;
    }
}