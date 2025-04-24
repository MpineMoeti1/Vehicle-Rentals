package com.example.demo1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password");
            return;
        }

        // Authenticate against the database
        User user = User.authenticate(username, password);
        if (user == null) {
            errorLabel.setText("Invalid username or password");
            return;
        }

        // Load the appropriate dashboard based on the user role
        try {
            loadDashboard(event, user);
        } catch (IOException e) {
            errorLabel.setText("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDashboard(ActionEvent event, User user) throws IOException {
        String dashboardPath;
        String title;

        // Determine dashboard based on user role
        if ("admin".equalsIgnoreCase(user.getRole())) {
            dashboardPath = "/com/example/demo1/admin_dashboard.fxml";
            title = "Admin Dashboard";

            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
            Parent root = loader.load();

            // Assuming DashboardController has a method to initialize with user data
            DashboardController controller = loader.getController();
            controller.initUserData(user); // Pass user data to the controller

            // Set the new scene with the loaded dashboard
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle(title);
            stage.show();
        } else if ("employee".equalsIgnoreCase(user.getRole())) {
            dashboardPath = "/com/example/demo1/employee_dashboard.fxml";
            title = "Employee Dashboard";

            // Load the dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardPath));
            Parent root = loader.load();

            // Initialize the employee dashboard controller
            EmployeeDashboardController controller = loader.getController();
            controller.initUserData(user); // Pass user data to the controller

            // Set the new scene with the loaded dashboard
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle(title);
            stage.show();
        } else {
            errorLabel.setText("Unknown user role: " + user.getRole());
        }
    }

    public void handleLogout(ActionEvent event) {
        // Redirect to the login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Error loading login screen: " + e.getMessage());
        }
    }
}