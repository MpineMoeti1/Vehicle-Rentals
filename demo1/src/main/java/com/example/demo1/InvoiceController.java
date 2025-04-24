package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

public class InvoiceController {

    @FXML
    private Label bookingIdLabel;
    @FXML
    private Label amountLabel;
    @FXML
    private Label paymentMethodLabel;
    @FXML
    private Label rentalDurationLabel;
    @FXML
    private Label additionalFeesLabel;
    @FXML
    private Label lateFeesLabel;
    @FXML
    private Label totalLabel;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "LS"));

    /**
     * This method initializes the invoice data and formats it properly.
     */
    @FXML
    public void initializeInvoice(String bookingId, double rentalFee, String paymentMethod,
                                  int rentalDuration, double additionalFees, double lateFees) {
        bookingIdLabel.setText("Booking ID: " + bookingId);
        amountLabel.setText("Rental Fee: " + formatCurrency(rentalFee));
        paymentMethodLabel.setText("Payment Method: " + paymentMethod);
        rentalDurationLabel.setText("Rental Duration: " + rentalDuration + " day(s)");
        additionalFeesLabel.setText("Additional Services: " + formatCurrency(additionalFees));
        lateFeesLabel.setText("Late Fees: " + formatCurrency(lateFees));

        double totalAmount = rentalFee + additionalFees + lateFees;
        totalLabel.setText("Total Amount: " + formatCurrency(totalAmount));
    }

    /**
     * This method handles closing the invoice window.
     */
    @FXML
    public void handleClose() {
        Stage stage = (Stage) bookingIdLabel.getScene().getWindow();
        stage.close();
    }

    /**
     * Format the amount as currency (e.g., M123.45).
     */
    private String formatCurrency(double amount) {
        return "M" + String.format("%.2f", amount);
    }
}
