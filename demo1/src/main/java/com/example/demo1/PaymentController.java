package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.time.LocalDate;

public class PaymentController {
    private String userRole; // Property to hold the user role
    private Stage currentStage; // Property to hold the current stage
    private Stage dashboardStage; // Property to hold the dashboard stage

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setCurrentStage(Stage stage) {
        this.currentStage = stage;
    }

    public void setDashboardStage(Stage stage) {
        this.dashboardStage = stage; // Store the reference to the dashboard stage
    }

    @FXML
    private TextField bookingIdField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField rentalFeeField;
    @FXML
    private TextField rentalDurationField;
    @FXML
    private ComboBox<String> paymentMethodComboBox;
    @FXML
    private TextField additionalServicesField;
    @FXML
    private TextField lateFeeField;
    @FXML
    private Label statusLabel;

    @FXML
    public void initialize() {
        paymentMethodComboBox.getItems().addAll("Cash", "Credit Card", "Online");
        paymentMethodComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void handleFetchBooking() {
        String bookingIdStr = bookingIdField.getText().trim();
        int bookingId = parseInt(bookingIdStr);
        if (bookingId <= 0) {
            statusLabel.setText("Invalid Booking ID.");
            return;
        }

        statusLabel.setText("You can enter rental details manually.");
        rentalFeeField.clear();
        rentalDurationField.clear();
        additionalServicesField.clear();
        lateFeeField.clear();
        amountField.clear();
    }

    @FXML
    public void handleCalculateTotal() {
        double rentalFee = parseDouble(rentalFeeField.getText());
        int rentalDuration = parseInt(rentalDurationField.getText());
        double additionalServices = parseDouble(additionalServicesField.getText());
        double lateFees = parseDouble(lateFeeField.getText());

        double totalAmount = (rentalFee * rentalDuration) + additionalServices + lateFees;

        if (totalAmount <= 0) {
            statusLabel.setText("Total amount must be greater than 0.0.");
            amountField.clear();
        } else {
            amountField.setText(String.format("%.2f", totalAmount));
            statusLabel.setText("Total amount calculated successfully.");
        }
    }

    @FXML
    public void handleProcessPayment() {
        int bookingId = parseInt(bookingIdField.getText());
        if (bookingId <= 0) {
            statusLabel.setText("Invalid Booking ID.");
            return;
        }

        double amount = parseDouble(amountField.getText());
        if (amount <= 0) {
            statusLabel.setText("Invalid payment amount.");
            return;
        }

        String paymentMethod = paymentMethodComboBox.getValue();
        if (paymentMethod == null || paymentMethod.isEmpty() ||
                (!paymentMethod.equals("Cash") && !paymentMethod.equals("Credit Card") && !paymentMethod.equals("Online"))) {
            statusLabel.setText("Invalid payment method.");
            return;
        }

        double rentalFee = parseDouble(rentalFeeField.getText());
        int rentalDuration = parseInt(rentalDurationField.getText());
        if (rentalFee <= 0 || rentalDuration <= 0) {
            statusLabel.setText("Rental fee and duration must be valid.");
            return;
        }

        double additionalServices = parseDouble(additionalServicesField.getText());
        double lateFees = parseDouble(lateFeeField.getText());

        Payment newPayment = new Payment(
                0,
                bookingId,
                amount,
                paymentMethod,
                LocalDate.now(),
                additionalServices,
                lateFees,
                "COMPLETED",
                rentalFee,
                rentalDuration
        );

        boolean success = Payment.processPayment(newPayment);
        if (success) {
            statusLabel.setText("Payment processed successfully.");
            openInvoiceWindow(newPayment);
        } else {
            statusLabel.setText("Payment processing failed.");
        }
    }

    private void openInvoiceWindow(Payment payment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo1/invoiceView.fxml"));
            Pane pane = loader.load();

            InvoiceController controller = loader.getController();
            controller.initializeInvoice(
                    String.valueOf(payment.getBookingId()),
                    payment.getAmount(),
                    payment.getPaymentMethod(),
                    payment.getRentalDuration(),
                    payment.getAdditionalServices(),
                    payment.getLateFee()
            );

            Stage stage = new Stage();
            stage.setTitle("Invoice Details");
            stage.setScene(new Scene(pane));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Failed to generate invoice.");
        }
    }

    @FXML
    public void handleBack() {
        // Check if dashboardStage has been set
        if (dashboardStage != null) {
            dashboardStage.show(); // Show the dashboard
            dashboardStage.toFront(); // Bring the dashboard to the front
        }

        // Close the current payment window
        Stage paymentStage = (Stage) bookingIdField.getScene().getWindow();
        paymentStage.close();
    }
    private double parseDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int parseInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (Exception e) {
            return 0;
        }
    }
}