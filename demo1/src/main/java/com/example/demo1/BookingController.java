package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

public class BookingController {
    private String userRole; // Add this field

    // Add setter for user role
    public void setUserRole(String role) {
        this.userRole = role;
    }

    @FXML
    private TextField customerIdField;
    @FXML
    private TextField vehicleIdField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private TextField totalPriceField;

    @FXML
    private TableView<Booking> bookingTable;
    @FXML
    private TableColumn<Booking, Integer> bookingIdColumn;
    @FXML
    private TableColumn<Booking, Integer> customerIdColumn;
    @FXML
    private TableColumn<Booking, Integer> vehicleIdColumn;
    @FXML
    private TableColumn<Booking, LocalDate> startDateColumn;
    @FXML
    private TableColumn<Booking, LocalDate> endDateColumn;
    @FXML
    private TableColumn<Booking, BigDecimal> totalPriceColumn;
    @FXML
    private TableColumn<Booking, Boolean> isActiveColumn;

    private ObservableList<Booking> bookingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Set up the table columns for display of Booking data
        bookingIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getBookingId()).asObject());
        customerIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCustomerId()).asObject());
        vehicleIdColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getVehicleId()).asObject());
        startDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getStartDate()));
        endDateColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getEndDate()));
        totalPriceColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTotalPrice()));
        isActiveColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleBooleanProperty(data.getValue().isActive()).asObject());

        loadBookings(null);
    }

    public void createBooking(ActionEvent event) {
        try {
            int customerId = Integer.parseInt(customerIdField.getText());
            int vehicleId = Integer.parseInt(vehicleIdField.getText());
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            BigDecimal totalPrice = new BigDecimal(totalPriceField.getText().trim());

            if (startDate == null || endDate == null) {
                showErrorDialog("Please select valid start and end dates.");
                return;
            }

            // Check vehicle availability
            if (!isVehicleAvailable(vehicleId, startDate, endDate)) {
                showErrorDialog("The vehicle is already booked for the selected dates.");
                return;
            }

            Booking booking = new Booking(0, customerId, vehicleId, startDate, endDate, totalPrice, true);

            if (addBooking(booking)) {
                loadBookings(null);
                showAlert("Booking created successfully.");
            } else {
                showErrorDialog("Failed to create booking.");
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter valid numeric values for Customer ID, Vehicle ID, and Total Price.");
        }
    }

    private boolean isVehicleAvailable(int vehicleId, LocalDate startDate, LocalDate endDate) {
        String sql = "SELECT COUNT(*) FROM bookings WHERE vehicle_id = ? AND is_active = 1 " +
                "AND ((start_date <= ? AND end_date >= ?) OR (start_date <= ? AND end_date >= ?))";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vehicleId);
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(endDate));
            stmt.setDate(5, Date.valueOf(startDate));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Available if count is 0
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Error checking vehicle availability: " + e.getMessage());
        }
        return false;
    }

    public void loadBookings(ActionEvent event) {
        bookingList.clear();
        String sql = "SELECT * FROM bookings WHERE is_active = 1";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Booking booking = new Booking(
                        rs.getInt("booking_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("vehicle_id"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getBigDecimal("total_price"),
                        rs.getBoolean("is_active")
                );
                bookingList.add(booking);
            }

            bookingTable.setItems(bookingList);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Error loading bookings: " + e.getMessage());
        }
    }

    public boolean addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (customer_id, vehicle_id, start_date, end_date, total_price, is_active) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, booking.getCustomerId());
            stmt.setInt(2, booking.getVehicleId());
            stmt.setDate(3, Date.valueOf(booking.getStartDate()));
            stmt.setDate(4, Date.valueOf(booking.getEndDate()));
            stmt.setBigDecimal(5, booking.getTotalPrice());
            stmt.setBoolean(6, booking.isActive());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void updateBooking(ActionEvent event) {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Please select a booking to update.");
            return;
        }

        try {
            int bookingId = selected.getBookingId();
            int customerId = Integer.parseInt(customerIdField.getText());
            int vehicleId = Integer.parseInt(vehicleIdField.getText());
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            BigDecimal totalPrice = new BigDecimal(totalPriceField.getText().trim());

            if (startDate == null || endDate == null) {
                showErrorDialog("Please select valid start and end dates.");
                return;
            }

            // Check vehicle availability for updating
            if (!isVehicleAvailable(vehicleId, startDate, endDate)) {
                showErrorDialog("The vehicle is already booked for the selected dates.");
                return;
            }

            String sql = "UPDATE bookings SET customer_id = ?, vehicle_id = ?, start_date = ?, end_date = ?, total_price = ? WHERE booking_id = ?";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, customerId);
                stmt.setInt(2, vehicleId);
                stmt.setDate(3, Date.valueOf(startDate));
                stmt.setDate(4, Date.valueOf(endDate));
                stmt.setBigDecimal(5, totalPrice);
                stmt.setInt(6, bookingId);

                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    loadBookings(null);
                    showAlert("Booking updated successfully.");
                } else {
                    showErrorDialog("No booking found with the specified ID to update.");
                }
            }
        } catch (NumberFormatException e) {
            showErrorDialog("Please enter valid numeric values for Customer ID, Vehicle ID, and Total Price.");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Error while updating booking: " + e.getMessage());
        }
    }

    public void cancelBooking(ActionEvent event) {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showErrorDialog("Please select a booking to cancel.");
            return;
        }

        int bookingId = selected.getBookingId();
        String sql = "UPDATE bookings SET is_active = 0 WHERE booking_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, bookingId);
            int cancelled = stmt.executeUpdate();
            if (cancelled > 0) {
                loadBookings(null);
                showAlert("Booking canceled successfully.");
            } else {
                showErrorDialog("Failed to cancel booking. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Error cancelling booking: " + e.getMessage());
        }
    }

    @FXML
    public void handleBackToDashboard(ActionEvent event) {
        try {
            // Assuming the AdminDashboard.fxml is in the same directory as this FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/employee_dashboard.fxml"));
            Pane pane = loader.load();

            // Assuming you want to replace the current scene
            Stage stage = (Stage) customerIdField.getScene().getWindow();
            stage.setScene(new Scene(pane));
            stage.setTitle("Admin Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Failed to load admin dashboard: " + e.getMessage());
        }
    }

    private void showErrorDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}