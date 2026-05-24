package com.example.rideease;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class PaymentController {

    @FXML
    private Text fareText;

    @FXML
    private Button payNowButton;

    private String startLocation;
    private String endLocation;
    private boolean isScheduledRide;
    private String vehicleType;
    private double fare;

    public void initializePayment(double fare, String startLocation, String endLocation, boolean isScheduledRide, String vehicleType) {
        this.fare = fare;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.isScheduledRide = isScheduledRide;
        this.vehicleType = vehicleType;
        fareText.setText("Fare: " + fare + " Taka");
    }

    @FXML
    private void handlePayNow() {
        boolean paymentSuccess = processPayment(fare);

        if (paymentSuccess) {
            saveRideDetails();
        } else {
            showAlert("Payment Failed", "Please try again.");
        }
    }

    private boolean processPayment(double fare) {
        // Integrate SSLCommerz payment API here
        return true; // Assume payment is successful for now
    }

    private void saveRideDetails() {
        ScheduledRideDAO rideDAO = new ScheduledRideDAO();
        rideDAO.addScheduledRide(
                LoggedInUser.getInstance().getUserId(),
                startLocation,
                endLocation,
                "2024-12-10 15:00:00", // Replace with actual journey time
                vehicleType,
                fare
        );
        showAlert("Payment Success", "Your ride has been confirmed.");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
