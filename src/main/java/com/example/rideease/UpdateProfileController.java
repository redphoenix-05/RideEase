package com.example.rideease;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UpdateProfileController {

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField phoneTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private Text errorText;

    private Connection connection;
    private String loggedInUserId;

    public void initialize() {
        connection = DatabaseConnection.getInstance().getConnection();
        loadUserProfile();
    }

    /**
     * Load the current user's profile into the text fields.
     */
    private void loadUserProfile() {
        try {
            LoggedInUser user = LoggedInUser.getInstance();
            if (user != null) {
                loggedInUserId = user.getUserId(); // Assuming you have a unique user ID
                nameTextField.setText(user.getName());
                phoneTextField.setText(user.getPhone());
                emailTextField.setText(user.getEmail());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to load user profile.");
        }
    }

    /**
     * Handle saving changes to the user's profile.
     */
    @FXML
    protected void handleSaveChanges() {
        String newName = nameTextField.getText().trim();
        String newPhone = phoneTextField.getText().trim();
        String newEmail = emailTextField.getText().trim();

        if (newName.isEmpty() || newPhone.isEmpty() || newEmail.isEmpty()) {
            showError("All fields are required.");
            return;
        }

        try {
            if (isPhoneOrEmailInUse(newPhone, newEmail)) {
                showError("Phone or email is already in use by another account.");
                return;
            }

            String updateQuery = "UPDATE users SET name = ?, phone = ?, email = ? WHERE user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newPhone);
            preparedStatement.setString(3, newEmail);
            preparedStatement.setString(4, loggedInUserId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                updateLoggedInUser(newName, newPhone, newEmail);
                showSuccess("Profile updated successfully.");
                // After saving changes, go back to myprofile.fxml
                loadMyProfile();
            } else {
                showError("Failed to update profile.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while updating the profile.");
        }
    }

    /**
     * Check if the phone or email is already in use by another user.
     */
    private boolean isPhoneOrEmailInUse(String phone, String email) throws Exception {
        String query = "SELECT COUNT(*) FROM users WHERE (phone = ? OR email = ?) AND user_id != ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, phone);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, loggedInUserId);

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
        return false;
    }

    /**
     * Update the logged-in user's details.
     */
    private void updateLoggedInUser(String name, String phone, String email) {
        LoggedInUser user = LoggedInUser.getInstance();
        if (user != null) {
            user.setName(name);
            user.setPhone(phone);
            user.setEmail(email);
        }
    }

    /**
     * Handle returning to the dashboard.
     */
    @FXML
    protected void handleBackButton() {
        try {
            Stage stage = (Stage) nameTextField.getScene().getWindow();
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), "/com/example/rideease/dashboard.fxml");
            stage.setScene(scene);
            stage.setTitle("Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load dashboard.");
        }
    }

    /**
     * Show an error message.
     */
    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }

    /**
     * Show a success message.
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Load the myprofile.fxml page after updating the profile.
     */
    private void loadMyProfile() {
        try {
            Stage stage = (Stage) nameTextField.getScene().getWindow();
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), "/com/example/rideease/myprofile.fxml");
            stage.setScene(scene);
            stage.setTitle("My Profile");
        } catch (IOException e) {
            e.printStackTrace();
            showError("Failed to load my profile.");
        }
    }
}
