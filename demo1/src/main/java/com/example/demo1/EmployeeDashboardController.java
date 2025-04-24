package com.example.demo1;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import javafx.scene.layout.Pane;

public class EmployeeDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Button handleBookingsButton;
    @FXML private Button handlePaymentsButton;
    @FXML private Button logoutButton;

    private User currentUser;

    @FXML
    public void initialize() {
    }

    public void initUserData(User user) {
        this.currentUser = user;
        updateUI();
    }

    private void updateUI() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getUsername());
            roleLabel.setText("Role: " + currentUser.getRole());
        }
    }

    @FXML
    private void handleBookings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/booking-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Manage Bookings");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load booking view", "Error details: " + e.getMessage());
        }
    }

    @FXML
    public void handlePayments() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/payment_view.fxml"));
            Pane pane = loader.load();

            PaymentController paymentController = loader.getController();
            paymentController.setUserRole("employee");
            paymentController.setCurrentStage((Stage) handlePaymentsButton.getScene().getWindow());

            Stage stage = new Stage();
            stage.setScene(new Scene(pane));
            stage.setTitle("Payment Management");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load payment view", "Error details: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        currentUser = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load login", "Error details: " + e.getMessage());
        }
    }

    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
