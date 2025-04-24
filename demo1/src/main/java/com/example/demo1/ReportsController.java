package com.example.demo1;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.chart.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.math.BigDecimal;
import java.util.Map;

public class ReportsController {

    // Table Views
    @FXML
    private TableView<Vehicle> availableVehiclesTable;

    @FXML
    private TableView<RentalHistory> rentalHistoryTable;

    @FXML
    private TableView<FinancialReport> revenueTable; // Update to FinancialReport

    // Table Columns
    @FXML
    private TableColumn<Vehicle, String> vehicleIdCol;

    @FXML
    private TableColumn<Vehicle, String> brandCol;

    @FXML
    private TableColumn<Vehicle, String> modelCol;

    @FXML
    private TableColumn<Vehicle, String> categoryCol;

    @FXML
    private TableColumn<Vehicle, Double> priceCol;

    @FXML
    private TableColumn<RentalHistory, String> rentalCustomerCol;

    @FXML
    private TableColumn<RentalHistory, String> rentalVehicleCol;

    @FXML
    private TableColumn<RentalHistory, LocalDate> startDateCol;

    @FXML
    private TableColumn<RentalHistory, LocalDate> endDateCol;

    @FXML
    private TableColumn<RentalHistory, String> statusCol;

    @FXML
    private TableColumn<RentalHistory, BigDecimal> amountCol;

    // Financial Report Table Columns
    @FXML
    private TableColumn<FinancialReport, String> monthCol;

    @FXML
    private TableColumn<FinancialReport, BigDecimal> totalRevenueCol;

    @FXML
    private TableColumn<FinancialReport, Integer> totalRentalsCol;

    // Charts
    @FXML
    private PieChart availabilityChart;

    @FXML
    private BarChart<String, Number> revenueBarChart;

    @FXML
    private LineChart<String, Number> revenueLineChart;

    private ObservableList<Vehicle> availableVehicles = FXCollections.observableArrayList();
    private ObservableList<RentalHistory> rentalHistory = FXCollections.observableArrayList();
    private ObservableList<FinancialReport> financialReports = FXCollections.observableArrayList(); // Update to FinancialReport

    @FXML
    public void initialize() {
        configureTables();
        loadAllData();
    }

    private void configureTables() {
        // Available Vehicles
        vehicleIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        brandCol.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("model"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

        // Rental History
        rentalCustomerCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        rentalVehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicleInfo"));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        // Financial Reports
        monthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        totalRevenueCol.setCellValueFactory(new PropertyValueFactory<>("totalRevenue"));
        totalRentalsCol.setCellValueFactory(new PropertyValueFactory<>("totalRentals"));
    }

    private void loadAllData() {
        loadAvailableVehicles();
        loadRentalHistory();
        loadRevenueData(); // Load financial reports
        updateCharts();
    }

    // Load Available Vehicles
    private void loadAvailableVehicles() {
        availableVehicles.clear();
        String sql = "SELECT * FROM vehicles WHERE availability_status = 1"; // Assuming 1 for available
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                availableVehicles.add(new Vehicle(
                        rs.getString("vehicle_id"),
                        rs.getString("brand"),
                        rs.getString("model"),
                        rs.getString("category"),
                        rs.getDouble("rental_price"),
                        rs.getInt("availability_status") == 1 // Assuming availability_status is 1 for available
                ));
            }
            availableVehiclesTable.setItems(availableVehicles);
        } catch (SQLException e) {
            showError("Database Error", "Failed to load vehicles", e.getMessage());
        }
    }

    // Load Rental History
    private void loadRentalHistory() {
        rentalHistory.clear();
        String sql = "SELECT b.*, c.name AS customer_name, v.brand, v.model " +
                "FROM bookings b " +
                "JOIN customers c ON b.customer_id = c.customer_id " +
                "JOIN vehicles v ON b.vehicle_id = v.vehicle_id";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                RentalHistory history = new RentalHistory(
                        rs.getString("customer_name"),
                        rs.getString("brand") + " " + rs.getString("model"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getBoolean("is_active"),
                        rs.getBigDecimal("total_price")
                );
                rentalHistory.add(history);
            }
            rentalHistoryTable.setItems(rentalHistory);
        } catch (SQLException e) {
            showError("Database Error", "Failed to load rental history", e.getMessage());
        }
    }

    // Load Revenue Data
    private void loadRevenueData() {
        financialReports.clear();
        String sql = "SELECT MONTH(start_date) AS month, SUM(total_price) AS total_revenue, COUNT(*) AS total_rentals " +
                "FROM bookings WHERE start_date IS NOT NULL " +
                "GROUP BY MONTH(start_date) ORDER BY MONTH(start_date)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int monthNumber = rs.getInt("month");
                String monthName = Month.of(monthNumber).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                BigDecimal totalRevenue = rs.getBigDecimal("total_revenue");
                int totalRentals = rs.getInt("total_rentals");

                financialReports.add(new FinancialReport(monthName, totalRevenue, totalRentals));
            }
            revenueTable.setItems(financialReports);
        } catch (SQLException e) {
            showError("Database Error", "Failed to load revenue", e.getMessage());
        }
    }

    // Update Charts
    private void updateCharts() {
        updateAvailabilityChart();
        updateCategoryBarChart();  // Changed to category chart
        updateRevenueLineChart();   // For revenue trend
    }

    private void updateCategoryBarChart() {
        Map<String, Integer> categoryData = Vehicle.getAvailableVehiclesByCategory();
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Available Vehicles");

        categoryData.forEach((category, count) -> {
            barSeries.getData().add(new XYChart.Data<>(category, count));
        });

        revenueBarChart.getData().clear();
        revenueBarChart.getData().add(barSeries);
    }

    private void updateRevenueLineChart() {
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Monthly Revenue");

        financialReports.forEach(report -> {
            lineSeries.getData().add(new XYChart.Data<>(report.getMonth(), report.getTotalRevenue()));
        });

        revenueLineChart.getData().clear();
        revenueLineChart.getData().add(lineSeries);
    }


    private void updateAvailabilityChart() {
        // Use the newly implemented methods to get counts
        int availableCount = Vehicle.getAvailableVehicleCount();
        int rentedCount = Vehicle.getRentedVehicleCount();

        availabilityChart.getData().clear();
        availabilityChart.getData().addAll(
                new PieChart.Data("Available (" + availableCount + ")", availableCount),
                new PieChart.Data("Rented (" + rentedCount + ")", rentedCount)
        );
    }

    private void updateRevenueCharts() {
        // For Bar Chart
        XYChart.Series<String, Number> barSeries = new XYChart.Series<>();
        barSeries.setName("Monthly Revenue");

        // For Line Chart
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Monthly Revenue Breakdown");

        financialReports.forEach(report -> {
            barSeries.getData().add(new XYChart.Data<>(report.getMonth(), report.getTotalRevenue()));
            lineSeries.getData().add(new XYChart.Data<>(report.getMonth(), report.getTotalRevenue()));
        });

        revenueBarChart.getData().clear();
        revenueBarChart.getData().add(barSeries);

        revenueLineChart.getData().clear();
        revenueLineChart.getData().add(lineSeries);
    }

    // Export functionality
    @FXML
    private void exportReport(ActionEvent event) {
        Object source = event.getSource();
        String type;
        ObservableList<?> data;

        if (source instanceof Button button) {
            switch (button.getText()) {
                case "Export Vehicles":
                    type = "Vehicles";
                    data = availableVehicles;
                    break;
                case "Export History":
                    type = "Rental_History";
                    data = rentalHistory;
                    break;
                case "Export Revenue":
                    type = "Revenue";
                    data = financialReports; // Updated to financialReports
                    break;
                default:
                    return; // Do nothing if button text doesn't match
            }
        } else {
            return; // Do nothing if source is not a button
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(type + "_" + System.currentTimeMillis() + ".csv");
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                if (data.size() > 0) {
                    // Write header
                    if (data.get(0) instanceof Vehicle) {
                        writer.write("ID,Brand,Model,Category,Price/Day,Available\n");
                        for (Vehicle v : (ObservableList<Vehicle>) data) {
                            writer.write(String.format("%s,%s,%s,%s,%.2f,%s\n",
                                    v.getId(), v.getBrand(), v.getModel(),
                                    v.getCategory(), v.getPricePerDay(), v.isAvailable()));
                        }
                    } else if (data.get(0) instanceof RentalHistory) {
                        writer.write("Customer,Vehicle,Start Date,End Date,Status,Amount\n");
                        for (RentalHistory rh : (ObservableList<RentalHistory>) data) {
                            writer.write(String.format("%s,%s,%s,%s,%s,%.2f\n",
                                    rh.getCustomerName(), rh.getVehicleInfo(),
                                    rh.getStartDate(), rh.getEndDate(),
                                    rh.getStatus(), rh.getAmount()));
                        }
                    } else if (data.get(0) instanceof FinancialReport) {
                        writer.write("Month,Total Revenue,Total Rentals\n");
                        for (FinancialReport fr : (ObservableList<FinancialReport>) data) {
                            writer.write(String.format("%s,%.2f,%d\n",
                                    fr.getMonth(), fr.getTotalRevenue().doubleValue(), fr.getTotalRentals()));
                        }
                    }
                }
                showAlert("Export Successful", "Data exported to:\n" + file.getAbsolutePath());
            } catch (IOException e) {
                showError("Export Failed", "Failed to save file", e.getMessage());
            }
        }
    }

    // Navigation
    @FXML
    private void handleBackToDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/demo1/admin_dashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showError("Navigation Error", "Failed to load dashboard", e.getMessage());
        }
    }

    // Helper classes
    public static class RentalHistory {
        private final String customerName;
        private final String vehicleInfo;
        private final LocalDate startDate;
        private final LocalDate endDate;
        private final boolean isActive;
        private final BigDecimal amount;

        public RentalHistory(String customerName, String vehicleInfo,
                             LocalDate startDate, LocalDate endDate,
                             boolean isActive, BigDecimal amount) {
            this.customerName = customerName;
            this.vehicleInfo = vehicleInfo;
            this.startDate = startDate;
            this.endDate = endDate;
            this.isActive = isActive;
            this.amount = amount;
        }

        // Getters
        public String getCustomerName() { return customerName; }
        public String getVehicleInfo() { return vehicleInfo; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public String getStatus() { return isActive ? "Active" : "Completed"; }
        public BigDecimal getAmount() { return amount; }
    }

    public static class FinancialReport {
        private final String month;
        private final BigDecimal totalRevenue;
        private final int totalRentals;

        public FinancialReport(String month, BigDecimal totalRevenue, int totalRentals) {
            this.month = month;
            this.totalRevenue = totalRevenue;
            this.totalRentals = totalRentals;
        }

        public String getMonth() { return month; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public int getTotalRentals() { return totalRentals; }
    }


    // Utility methods
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}