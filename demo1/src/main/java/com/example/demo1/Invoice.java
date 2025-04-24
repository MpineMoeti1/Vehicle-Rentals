package com.example.demo1;

import java.time.LocalDate;

public class Invoice {
    private int invoiceId;
    private String customerName;
    private double amount;
    private LocalDate date;

    public Invoice(int invoiceId, String customerName, double amount, LocalDate date) {
        this.invoiceId = invoiceId;
        this.customerName = customerName;
        this.amount = amount;
        this.date = date;
    }

    public String getFormattedInvoice() {
        return String.format("Invoice ID: %d\nCustomer Name: %s\nAmount: %.2f\nDate: %s",
                invoiceId, customerName, amount, date);
    }
}