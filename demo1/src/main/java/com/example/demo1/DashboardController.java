package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;

public class DashboardController {

    // UI Elements
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Button manageVehiclesButton;
    @FXML private Button manageCustomersButton;
    @FXML private Button handleBookingsButton;
    @FXML private Button handlePaymentsButton;
    @FXML private Button viewReportsButton;
    @FXML private VBox adminSection;
    @FXML private Button logoutButton;

    // Current user data
    private User currentUser;

    @FXML
    public void initialize() {
        // Initial setup if needed
    }

    public void initUserData(User user) {
        this.currentUser = user;
        updateUI();
        configureRoleAccess();
    }

    private void updateUI() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername());
            roleLabel.setText("Role: " + currentUser.getRole());
        }
    }

    private void configureRoleAccess() {
        if (currentUser == null) return;

        boolean isAdmin = "admin".equalsIgnoreCase(currentUser.getRole());

        // Safely handle admin section visibility
        if (adminSection != null) {
            adminSection.setVisible(isAdmin);
        }

        // Safely handle button states
        if (manageVehiclesButton != null) {
            manageVehiclesButton.setDisable(!isAdmin);
        }
        if (manageCustomersButton != null) {
            manageCustomersButton.setDisable(!isAdmin);
        }
        if (viewReportsButton != null) {
            viewReportsButton.setDisable(!isAdmin);
        }
    }

    // Navigation methods
    @FXML
    private void handleManageVehicles(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("vehicle-management.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Vehicle Management");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load vehicle management", "Error details: " + e.getMessage());
        }
    }

    @FXML
    private void handleManageCustomers(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/customer_management.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Manage Customers");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load customer management", "Error details: " + e.getMessage());
        }
    }

    @FXML
    private void handleBookings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/booking-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Manage Bookings");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load booking view", "Error details: " + e.getMessage());
        }
    }

    @FXML
    private void handlePayments(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/payment_view.fxml"));
            Parent root = loader.load();

            // Obtain the PaymentController instance
            PaymentController paymentController = loader.getController();

            // Set the dashboard stage in the PaymentController
            paymentController.setDashboardStage((Stage) welcomeLabel.getScene().getWindow());

            // Configure the payment stage
            Stage stage = new Stage();
            stage.setTitle("Handle Payments");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load payment view", "Could not find payment_view.fxml at the specified path.");
        }
    }    @FXML
    private void handleViewReports(ActionEvent event) throws IOException {
        loadScene("/com/example/demo1/reports.fxml", "View Reports");
    }

    @FXML
    private void handleLogout() throws IOException {
        currentUser = null;
        loadScene("/com/example/demo1/login.fxml", "Login");
    }

    private void loadScene(String fxmlPath, String title) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        Stage stage = (Stage) welcomeLabel.getScene().getWindow();
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle(title);
        stage.show();
    }

    // Alert handling
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Booking handling methods
    @FXML
    private void handleAddBooking(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/add_booking.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add Booking");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load add booking", "Error details: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelBooking(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/cancel_booking.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Cancel Booking");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load cancel booking", "Error details: " + e.getMessage());
        }
    }

    @FXML
    private void handleGetBookingById(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/get_booking_by_id.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Get Booking by ID");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load get booking by ID", "Error details: " + e.getMessage());
        }
    }

    @FXML
    private void handleGetAllActiveBookings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/all_active_bookings.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("All Active Bookings");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load all active bookings", "Error details: " + e.getMessage());
        }
    }
}