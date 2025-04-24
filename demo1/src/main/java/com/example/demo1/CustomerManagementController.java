package com.example.demo1;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.util.List;

public class CustomerManagementController {

    @FXML private TextField idField, nameField, contactField, licenseField, rentalHistoryField, searchField;
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, String> idCol, nameCol, contactCol, licenseCol, rentalHistoryCol;
    @FXML private Button btnBack;

    private ObservableList<Customer> customerList;

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId")); // customer_id
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name")); // name
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contact")); // contact
        licenseCol.setCellValueFactory(new PropertyValueFactory<>("licenseNumber")); // license_number
        rentalHistoryCol.setCellValueFactory(new PropertyValueFactory<>("rentalHistory")); // rental_history

        loadCustomers();

        customerTable.setOnMouseClicked(e -> {
            Customer selected = customerTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                idField.setText(selected.getCustomerId());
                nameField.setText(selected.getName());
                contactField.setText(selected.getContact());
                licenseField.setText(selected.getLicenseNumber());
                rentalHistoryField.setText(selected.getRentalHistory());
            }
        });
    }

    @FXML
    public void handleAdd() {
        Customer customer = getCustomerFromInput();
        if (Customer.addCustomer(customer)) {
            showAlert("Customer added successfully.");
            loadCustomers();
            clearFields();
        } else {
            showAlert("Failed to add customer.");
        }
    }

    @FXML
    public void handleUpdate() {
        Customer customer = getCustomerFromInput();
        if (Customer.updateCustomer(customer)) {
            showAlert("Customer updated successfully.");
            loadCustomers();
            clearFields();
        } else {
            showAlert("Failed to update customer.");
        }
    }

    @FXML
    public void handleDelete() {
        String id = idField.getText();
        if (id.isEmpty()) {
            showAlert("Enter the customer ID to delete.");
            return;
        }

        if (Customer.deleteCustomer(id)) {
            showAlert("Customer deleted successfully.");
            loadCustomers();
            clearFields();
        } else {
            showAlert("Failed to delete customer.");
        }
    }

    @FXML
    public void handleSearch() {
        String term = searchField.getText();
        List<Customer> result = Customer.searchCustomers(term);
        customerList = FXCollections.observableArrayList(result);
        customerTable.setItems(customerList);
    }

    @FXML
    public void loadCustomers() {
        List<Customer> all = Customer.getAllCustomers();
        customerList = FXCollections.observableArrayList(all);
        customerTable.setItems(customerList);
    }

    private Customer getCustomerFromInput() {
        String id = idField.getText();
        String name = nameField.getText();
        String contact = contactField.getText(); // Changed to contactField
        String license = licenseField.getText(); // Changed to licenseField
        String rental = rentalHistoryField.getText(); // Changed to rentalHistoryField

        return new Customer(id, name, contact, license, rental);
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        contactField.clear(); // Changed to contactField
        licenseField.clear(); // Changed to licenseField
        rentalHistoryField.clear(); // Changed to rentalHistoryField
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
            Parent dashboardRoot = FXMLLoader.load(getClass().getResource("/com/example/demo1/admin_dashboard.fxml"));
            Stage stage = (Stage) idField.getScene().getWindow();
            stage.setScene(new Scene(dashboardRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Failed to load the main dashboard.");
        }
    }
}