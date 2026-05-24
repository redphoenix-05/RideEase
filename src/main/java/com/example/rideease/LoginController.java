package com.example.rideease;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginController {

    @FXML
    private TextField emailPhoneField;

    @FXML
    private PasswordField passwordField;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/rideease-1";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ariyan123";

    @FXML
    protected void handleLoginButtonClick() {
        String emailOrPhone = emailPhoneField.getText();
        String password = passwordField.getText();

        if (emailOrPhone.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required!");
            return;
        }

        LoggedInUser user = validateLogin(emailOrPhone, password);

        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid email/phone or password!");
        } else {
            LoggedInUser.setInstance(user); // Set the logged-in user globally
            switchToScene("/com/example/rideease/dashboard.fxml"); // Navigate to dashboard
        }
    }

    private LoggedInUser validateLogin(String emailOrPhone, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT user_id, name, email, phone, gender, password FROM users WHERE (email = ? OR phone = ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, emailOrPhone);
            preparedStatement.setString(2, emailOrPhone);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                if (storedPassword.equals(password)) {
                    // Create a LoggedInUser object for the logged-in user
                    return new LoggedInUser(
                            resultSet.getString("user_id"), // Fetch user ID
                            resultSet.getString("name"),
                            resultSet.getString("email"),
                            resultSet.getString("phone"),
                            resultSet.getString("gender")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
        }
        return null;
    }

    @FXML
    protected void handleBackButtonClick() {
        switchToScene("/com/example/rideease/scene1.fxml");
    }

    @FXML
    protected void handleForgotPassword() {
        switchToScene("/com/example/rideease/forgotPassword.fxml");
    }

    private void switchToScene(String fxmlFile) {
        try {
            Stage stage = (Stage) emailPhoneField.getScene().getWindow();
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), fxmlFile);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
