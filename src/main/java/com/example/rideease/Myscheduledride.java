package com.example.rideease;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Myscheduledride {

    @FXML
    private Text titleText;

    @FXML
    private TableView<ScheduledRide> scheduledRidesTable;

    @FXML
    private TableColumn<ScheduledRide, Integer> serialNoColumn;

    @FXML
    private TableColumn<ScheduledRide, String> journeyDateColumn;

    @FXML
    private TableColumn<ScheduledRide, String> journeyTimeColumn;

    @FXML
    private TableColumn<ScheduledRide, String> startLocationColumn;

    @FXML
    private TableColumn<ScheduledRide, String> endLocationColumn;

    @FXML
    private Button backButton;

    @FXML
    private TableColumn<ScheduledRide, Button> deleteColumn;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/rideease-1";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ariyan123";

    // Initialize the table and load scheduled rides for the current user
    public void initialize() {
        // Set up the columns
        serialNoColumn.setCellValueFactory(new PropertyValueFactory<>("serialNo"));
        journeyDateColumn.setCellValueFactory(new PropertyValueFactory<>("journeyDate"));
        journeyTimeColumn.setCellValueFactory(new PropertyValueFactory<>("journeyTime"));
        startLocationColumn.setCellValueFactory(new PropertyValueFactory<>("startLocation"));
        endLocationColumn.setCellValueFactory(new PropertyValueFactory<>("endLocation"));
        deleteColumn.setCellValueFactory(new PropertyValueFactory<>("deleteButton"));

        loadScheduledRides();
    }

    @FXML
    protected void handleBackButtonClick() {
        switchToScene("/com/example/rideease/dashboard.fxml");
    }

    private void switchToScene(String fxmlFile) {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), fxmlFile);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get the current user's ID (this method is assumed to exist)
    private String getUserId() {
        LoggedInUser user = LoggedInUser.getInstance();
        String userId = user.getUserId();
        return userId;
    }

    // Load scheduled rides for the logged-in user
    private void loadScheduledRides() {
        List<ScheduledRide> scheduledRides = new ArrayList<>();
        String userId = getUserId();  // Get the logged-in user's ID

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Modify the query to filter by user_id
            String query = "SELECT * FROM schldride WHERE user_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, userId);  // Set the user_id parameter
                try (ResultSet resultSet = statement.executeQuery()) {
                    int serialNo = 1;
                    while (resultSet.next()) {
                        int rideId = resultSet.getInt("id");
                        String journeyDate = resultSet.getString("journey_date");
                        String journeyTime = resultSet.getString("journey_time");
                        String startLocation = resultSet.getString("start_location");
                        String endLocation = resultSet.getString("end_location");

                        // Create a delete button for each row
                        Button deleteButton = new Button("Delete");
                        deleteButton.setOnAction(event -> deleteScheduledRide(rideId));

                        // Add the ride to the list
                        scheduledRides.add(new ScheduledRide(serialNo++, journeyDate, journeyTime, startLocation, endLocation, deleteButton));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set the data to the table
        scheduledRidesTable.getItems().setAll(scheduledRides);
    }

    // Delete the scheduled ride from the database
    private void deleteScheduledRide(int rideId) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "DELETE FROM schldride WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, rideId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected > 0) {
                    // Reload the table after deletion
                    loadScheduledRides();
                    showAlert("Success", "Ride deleted successfully.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while deleting the ride.");
        }
    }

    // Show an alert message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



    // Inner class to represent a scheduled ride
    public static class ScheduledRide {
        private final int serialNo;
        private final String journeyDate;
        private final String journeyTime;
        private final String startLocation;
        private final String endLocation;
        private final Button deleteButton;

        public ScheduledRide(int serialNo, String journeyDate, String journeyTime, String startLocation, String endLocation, Button deleteButton) {
            this.serialNo = serialNo;
            this.journeyDate = journeyDate;
            this.journeyTime = journeyTime;
            this.startLocation = startLocation;
            this.endLocation = endLocation;
            this.deleteButton = deleteButton;
        }

        public int getSerialNo() {
            return serialNo;
        }

        public String getJourneyDate() {
            return journeyDate;
        }

        public String getJourneyTime() {
            return journeyTime;
        }

        public String getStartLocation() {
            return startLocation;
        }

        public String getEndLocation() {
            return endLocation;
        }

        public Button getDeleteButton() {
            return deleteButton;
        }
    }
}
