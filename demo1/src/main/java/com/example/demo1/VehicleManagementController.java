package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class VehicleManagementController {

    @FXML private TextField idField, brandField, modelField, priceField, searchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private CheckBox availableCheckbox;
    @FXML private TableView<Vehicle> vehicleTable;
    @FXML private TableColumn<Vehicle, String> idCol, brandCol, modelCol, categoryCol;
    @FXML private TableColumn<Vehicle, Double> priceCol;
    @FXML private TableColumn<Vehicle, Boolean> availabilityCol;

    private ObservableList<Vehicle> vehicleList;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("available"));

        categoryComboBox.setItems(FXCollections.observableArrayList("Sedan", "SUV","Bike","Car", "Truck", "Van", "Convertible", "Hatchback"));

        loadVehicles();

        vehicleTable.setOnMouseClicked(e -> {
            Vehicle selected = vehicleTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                idField.setText(selected.getId());
                brandField.setText(selected.getBrand());
                modelField.setText(selected.getModel());
                categoryComboBox.setValue(selected.getCategory());
                priceField.setText(String.valueOf(selected.getPricePerDay()));
                availableCheckbox.setSelected(selected.isAvailable());
            }
        });
    }

    @FXML
    public void handleAdd() {
        Vehicle vehicle = getVehicleFromInput();
        if (vehicle == null) return;

        if (Vehicle.addVehicle(vehicle)) {
            showAlert("Vehicle added successfully.");
            loadVehicles();
            clearFields();
        } else {
            showAlert("Failed to add vehicle.");
        }
    }

    @FXML
    public void handleUpdate() {
        Vehicle vehicle = getVehicleFromInput();
        if (vehicle == null) return;

        if (Vehicle.updateVehicle(vehicle)) {
            showAlert("Vehicle updated successfully.");
            loadVehicles();
            clearFields();
        } else {
            showAlert("Failed to update vehicle.");
        }
    }

    @FXML
    public void handleDelete() {
        String id = idField.getText();
        if (id.isEmpty()) {
            showAlert("Enter the vehicle ID to delete.");
            return;
        }

        if (Vehicle.deleteVehicle(id)) {
            showAlert("Vehicle deleted successfully.");
            loadVehicles();
            clearFields();
        } else {
            showAlert("Failed to delete vehicle.");
        }
    }

    @FXML
    public void handleSearch() {
        String term = searchField.getText().trim();
        List<Vehicle> result = Vehicle.searchVehicles(term);
        vehicleList = FXCollections.observableArrayList(result);
        vehicleTable.setItems(vehicleList);
    }

    @FXML
    public void loadVehicles() {
        List<Vehicle> all = Vehicle.getAllVehicles();
        vehicleList = FXCollections.observableArrayList(all);
        vehicleTable.setItems(vehicleList);
    }

    private Vehicle getVehicleFromInput() {
        String id = idField.getText().trim();
        String brand = brandField.getText().trim();
        String model = modelField.getText().trim();
        String category = categoryComboBox.getValue();
        if (id.isEmpty() || brand.isEmpty() || model.isEmpty() || category == null || priceField.getText().isEmpty()) {
            showAlert("Please fill in all fields.");
            return null;
        }

        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Invalid price format.");
            return null;
        }

        boolean available = availableCheckbox.isSelected();
        return new Vehicle(id, brand, model, category, price, available);
    }

    private void clearFields() {
        idField.clear();
        brandField.clear();
        modelField.clear();
        categoryComboBox.getSelectionModel().clearSelection();
        priceField.clear();
        availableCheckbox.setSelected(false);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleBackButtonAction() {
        try {
            // Get the current stage (window)
            Stage stage = (Stage) idField.getScene().getWindow();

            // Check if the current scene is already the dashboard scene
            if (!(stage.getScene().getRoot() instanceof VBox) || !((VBox) stage.getScene().getRoot()).getId().equals("dashboardRoot")) {
                // Load the dashboard scene if it's not already displayed
                Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/demo1/admin_dashboard.fxml"));
                stage.setScene(new Scene(dashboardRoot));
                dashboardRoot.setId("dashboardRoot");  // Set an ID for the root node to identify the dashboard
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load dashboard.");
        }
    }


}
