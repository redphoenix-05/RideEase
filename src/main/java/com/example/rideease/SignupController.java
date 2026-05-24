package com.example.rideease;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignupController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private RadioButton maleRadioButton;
    @FXML
    private RadioButton femaleRadioButton;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/rideease-1";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "ariyan123";

    @FXML
    public void handleSignupButtonClick() {
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        String gender = maleRadioButton.isSelected() ? "Male" : femaleRadioButton.isSelected() ? "Female" : null;
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!validateInput(name, phone, email, gender, password, confirmPassword)) {
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if the email already exists
            String checkEmailQuery = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement checkEmailStatement = connection.prepareStatement(checkEmailQuery);
            checkEmailStatement.setString(1, email);
            ResultSet resultSet = checkEmailStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Email address is already registered.");
                return;
            }

            // Insert the user details into the database
            String insertQuery = "INSERT INTO users (name, phone, email, gender, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, phone);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, gender);
            preparedStatement.setString(5, password);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Signup successful!");
                navigateToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Signup failed. Please try again.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    protected void handleBackButtonClick() {
        switchToScene("/com/example/rideease/scene1.fxml");
    }

    private void switchToScene(String fxmlFile) {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow(); // Use nameField as a reference
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), fxmlFile);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean validateInput(String name, String phone, String email, String gender, String password, String confirmPassword) {
        if (name == null || name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Name cannot be empty.");
            return false;
        }
        if (phone == null || phone.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Phone number cannot be empty.");
            return false;
        }
        if (email == null || email.isEmpty() || !email.contains("@")) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please enter a valid email address.");
            return false;
        }
        if (gender == null) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Please select a gender.");
            return false;
        }
        if (password == null || password.isEmpty() || password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Password must be at least 6 characters long.");
            return false;
        }
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Passwords do not match.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void navigateToLogin() {
        try {
            Stage currentStage = (Stage) nameField.getScene().getWindow();
            currentStage.setScene(UiSceneFactory.loadResponsiveScene(getClass(), "/com/example/rideease/login.fxml"));
            currentStage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Unable to load the login screen: " + e.getMessage());
        }
    }
}