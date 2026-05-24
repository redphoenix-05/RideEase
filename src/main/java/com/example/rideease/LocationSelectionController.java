package com.example.rideease;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import static java.lang.Math.abs;
import static java.lang.Math.round;

public class LocationSelectionController {

    @FXML
    private TextField startLocationTextField;

    @FXML
    private TextField endLocationTextField;

    @FXML
    private WebView mapView;

    @FXML
    private Text fareText;

    @FXML
    private Button confirmButton;

    @FXML
    private Label dateLabel;

    @FXML
    private TextField dateTextField;

    @FXML
    private TextField timeTextField;

    @FXML
    private Label timeLabel;

    private String vehicleType;

    private boolean isScheduledRide;

    private double fare;

    private static String API_KEY = Config.get("GOOGLE_API_KEY", "AIzaSyAj7HONwPoKaqW7j1BoHcdYvrBa9ArMv0U");

    private static final String STORE_ID = Config.get("STORE_ID", "ridee67573b7e3a7d3"); // Replace with your test Store ID
    private static final String STORE_PASSWORD = Config.get("STORE_PASSWORD", "ridee67573b7e3a7d3@ssl"); // Replace with your test Password
    private static final String TRANSACTION_URL = Config.get("TRANSACTION_URL", "https://sandbox.sslcommerz.com/gwprocess/v4/api.php");

    private static final String DB_URL = Config.get("DB_URL", "jdbc:mysql://localhost:3306/rideease-1");
    private static final String DB_USER = Config.get("DB_USER", "root");
    private static final String DB_PASSWORD = Config.get("DB_PASSWORD", "ariyan123");

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public void setScheduledRide(boolean isScheduledRide) {
        this.isScheduledRide = isScheduledRide;
        dateLabel.setVisible(isScheduledRide);
        dateTextField.setVisible(isScheduledRide);
        timeLabel.setVisible(isScheduledRide);
        timeTextField.setVisible(isScheduledRide);
    }

    @FXML
    private void handlebackbutton() {
        try {
            Stage stage = (Stage) fareText.getScene().getWindow();
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), "/com/example/rideease/dashboard.fxml");
            stage.setScene(scene);
            stage.setTitle("Dashboard");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String createPaymentSession() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            Map<String, String> params = new HashMap<>();
            params.put("store_id", STORE_ID);
            params.put("store_passwd", STORE_PASSWORD);
            params.put("total_amount", "100");
            params.put("currency", "BDT");
            params.put("tran_id", "TEST_" + System.currentTimeMillis());
            params.put("success_url", Config.get("PAYMENT_SUCCESS_URL", "http://127.0.0.1:1000/success_done"));
            params.put("fail_url", Config.get("PAYMENT_FAIL_URL", "http://127.0.0.1:1000/faildone"));
            params.put("cancel_url", Config.get("PAYMENT_CANCEL_URL", "http://127.0.0.1:1000/canceldone"));
            params.put("cus_name", "Test Customer");
            params.put("cus_email", "test@example.com");
            params.put("cus_add1", "Test Address");
            params.put("cus_city", "Dhaka");
            params.put("cus_postcode", "1207");
            params.put("cus_country", "Bangladesh");
            params.put("cus_phone", "01700000000");
            params.put("shipping_method", "NO");
            params.put("product_name", "ride");
            params.put("product_category", "service");
            params.put("product_profile", "general");

            String form = params.entrySet().stream()
                    .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                            URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(TRANSACTION_URL))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String responseJson = response.body();
            System.out.println(responseJson);
            if (responseJson.contains("\"GatewayPageURL\"")) {
                int startIndex = responseJson.indexOf("\"GatewayPageURL\":\"") + 18;
                int endIndex = responseJson.indexOf("\"", startIndex);
                return responseJson.substring(startIndex, endIndex).replace("\\/", "/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @FXML
    private void handleProceedToCheckout() {
        try {
            String paymentUrl = createPaymentSession();
            if (paymentUrl != null) {
                mapView.getEngine().load(paymentUrl);
            } else {
                showAlert("Failed", "Failed to create payment request.");
                return;
            }

            mapView.getEngine().locationProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.contains("success_done")) {
                    mapView.getEngine().loadContent("");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Payment");
                    alert.setHeaderText(null);
                    alert.setContentText("Payment Completed Successfully!");
                    alert.showAndWait();
                    if (isScheduledRide) {
                        saveJourneyDetails();
                    }
                    try {
                        Stage stage = (Stage) mapView.getScene().getWindow();
                        Scene dashboardScene = UiSceneFactory.loadResponsiveScene(getClass(), "/com/example/rideease/dashboard.fxml");
                        stage.setScene(dashboardScene);
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert("Error", "Unable to load the dashboard. Please try again.");
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while processing the payment.");
        }
    }

    private void saveJourneyDetails() {
        String journeyDate = dateTextField.getText();
        String journeyTime = timeTextField.getText();
        String startLocation = startLocationTextField.getText();
        String endLocation = endLocationTextField.getText();

        LoggedInUser user = LoggedInUser.getInstance();
        // Get the user_id of the logged-in user
        String userId = user.getUserId();  // Assuming getUserId() returns the logged-in user's ID

        if (journeyDate.isEmpty() || journeyTime.isEmpty() || startLocation.isEmpty() || endLocation.isEmpty()) {
            showAlert("Invalid Input", "Please provide all required details.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO schldride (journey_date, journey_time, vehicle_type, start_location, end_location, fare, user_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, journeyDate);
                statement.setString(2, journeyTime);
                statement.setString(3, vehicleType);  // Store the vehicle type
                statement.setString(4, startLocation);
                statement.setString(5, endLocation);
                statement.setDouble(6, fare);
                statement.setString(7, userId);  // Set the user_id

                int rowsInserted = statement.executeUpdate();
                if (rowsInserted > 0) {
                    System.out.println("Journey details saved successfully!");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while saving the journey details.");
        }
    }



    private double calculateFare(String vehicleType, double distance) {
        return round(vehicleType.equals("bike") ? distance * 30 : distance * 60);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleOnMap() {
        String startLocation = startLocationTextField.getText();
        String endLocation = endLocationTextField.getText();

        if (startLocation.isEmpty() || endLocation.isEmpty()) {
            showAlert("Invalid Input", "Please enter both start and end locations.");
            return;
        }

        // Open Google Maps with the route between the start and end locations
        String googleMapsURL = "https://www.google.com/maps/dir/" + startLocation + "/" + endLocation;
        mapView.getEngine().load(googleMapsURL);

        // Get the distance between the start and end locations
        double distance = getDistance(startLocation, endLocation);

        // Calculate the fare based on the selected vehicle and the distance
        fare = calculateFare(vehicleType, distance);
        fareText.setText("Fare: " + fare + " Taka");

        // Make the fare text and proceed to checkout button visible
        fareText.setVisible(true);
        confirmButton.setVisible(true);
    }


public static double getDistance(String origin, String destination) {
        try {
            // Prepare the URL for Google Maps Distance Matrix API
            String urlStr = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + origin + "&destinations=" + destination + "&key=" + API_KEY;

            // Create HttpClient and make a request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlStr))
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse the JSON response using Gson
            JsonObject jsonResponse = new Gson().fromJson(response.body(), JsonObject.class);

            // Extract the distance from the response (in meters)
            JsonArray rows = jsonResponse.getAsJsonArray("rows");
            JsonObject elements = rows.get(0).getAsJsonObject().getAsJsonArray("elements").get(0).getAsJsonObject();
            String status = elements.get("status").getAsString();

            if ("OK".equals(status)) {
                int distanceInMeters = elements.getAsJsonObject("distance").get("value").getAsInt();
                // Convert meters to kilometers
                return abs(distanceInMeters / 1000.0);  // Return distance in kilometers
            } else {
                System.out.println("Error calculating distance: " + status);
                return -1; // Return -1 in case of error
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
