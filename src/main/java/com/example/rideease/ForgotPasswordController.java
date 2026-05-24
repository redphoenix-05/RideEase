package com.example.rideease;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;

public class ForgotPasswordController {

    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button updatePasswordButton;
    @FXML
    private Text errorText;

    @FXML
    private Text newPasswordLabel;
    @FXML
    private Text confirmPasswordLabel;

    private Connection connection;

    public ForgotPasswordController() {
        try {
            String url = "jdbc:mysql://localhost:3306/rideease-1";
            String user = "root";
            String password = "ariyan123";
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        Stage stage = (Stage) emailTextField.getScene().getWindow();
        stage.close();

        try {
            Stage newStage = new Stage();
            newStage.setScene(UiSceneFactory.loadResponsiveScene(getClass(), "/com/example/rideease/login.fxml"));
            newStage.setTitle("Login");
            newStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleFindAccountButton() {
        String email = emailTextField.getText().trim();

        if (email.isEmpty()) {
            showError("Please enter an email address.");
            return;
        }

        try {
            String query = "SELECT * FROM users WHERE email = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                newPasswordLabel.setVisible(true);
                confirmPasswordLabel.setVisible(true);
                newPasswordField.setVisible(true);
                confirmPasswordField.setVisible(true);
                updatePasswordButton.setVisible(true);
                errorText.setVisible(false);
            } else {
                showError("No account found with this email.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error. Please try again later.");
        }
    }

    @FXML
    private void handleUpdatePasswordButton() {
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please enter both passwords.");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords don't match.");
            return;
        }

        try {
            String email = emailTextField.getText().trim();

            String updateQuery = "UPDATE users SET password = ? WHERE email = ?";
            PreparedStatement stmt = connection.prepareStatement(updateQuery);
            stmt.setString(1, newPassword);
            stmt.setString(2, email);

            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                showAlert("Password updated successfully. Please log in with your new password.");
                handleBackButton();
            } else {
                showError("Failed to update the password. Please try again.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database error. Please try again later.");
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
