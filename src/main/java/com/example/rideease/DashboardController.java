package com.example.rideease;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class DashboardController {

    @FXML
    private MenuButton userMenuButton;

    @FXML
    private RadioButton bikeRadioButton;

    @FXML
    private RadioButton carRadioButton;

    @FXML
    private Button bookRideNowButton; // Button for immediate booking
    @FXML
    private Button scheduleRideButton; // Button for scheduled rides

    private ToggleGroup vehicleToggleGroup;

    private String loggedInUserName;

    public void initialize() {
        vehicleToggleGroup = new ToggleGroup();
        bikeRadioButton.setToggleGroup(vehicleToggleGroup);
        carRadioButton.setToggleGroup(vehicleToggleGroup);

        // Set default selected vehicle (optional)
        bikeRadioButton.setSelected(true);

        // Set logged-in user
        setLoggedInUser();
    }


    public void setLoggedInUser() {
        LoggedInUser user = LoggedInUser.getInstance(); // Singleton to get logged-in user
        if (user != null) {
            this.loggedInUserName = user.getName();
            userMenuButton.setText(loggedInUserName);
        }
    }

    @FXML
    protected void handleBookRideNow() {
        navigateToLocationSelection(false); // False means "Book Now"
    }

    @FXML
    protected void handleScheduleRide() {
        navigateToLocationSelection(true); // True means "Schedule Ride"
    }


    private void navigateToLocationSelection(boolean isScheduledRide) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/rideease/locationSelection.fxml"));
            Parent root = loader.load();

            // Pass data to LocationSelectionController
            LocationSelectionController locationController = loader.getController();
            locationController.setVehicleType(getSelectedVehicleType());
            locationController.setScheduledRide(isScheduledRide);

            // Navigate to LocationSelection scene
            Stage stage = (Stage) bookRideNowButton.getScene().getWindow();
            Scene scene = UiSceneFactory.createResponsiveScene(root);
            stage.setScene(scene);
            stage.setTitle(isScheduledRide ? "Schedule a Ride" : "Book a Ride Now");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to navigate to the location selection screen.");
        }
    }

    private String getSelectedVehicleType() {
        if (bikeRadioButton.isSelected()) {
            return "bike";
        } else if (carRadioButton.isSelected()) {
            return "car";
        }
        return null; // Default case (shouldn't happen with a proper toggle group setup)
    }

    @FXML
    protected void handleMyProfile() {
        loadScene("/com/example/rideease/myprofile.fxml", "My Profile");
    }

    /**
     * Handles navigation to the user's scheduled rides screen.
     */
    @FXML
    protected void handleMyScheduledRide() {
        loadScene("/com/example/rideease/myscheduledride.fxml", "My Scheduled Rides");
    }

    @FXML
    protected void handleLogout() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation");
        alert.setHeaderText("Are you sure you want to log out?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                loadScene("/com/example/rideease/login.fxml", "Login");
            }
        });
    }

    private void loadScene(String fxmlFile, String title) {
        try {
            Stage stage = (Stage) userMenuButton.getScene().getWindow();
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), fxmlFile);
            stage.setScene(scene);
            stage.setTitle(title);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load " + title + " screen.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
