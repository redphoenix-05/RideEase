package com.example.rideease;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class MyProfileController {

    @FXML
    private Text nameText;

    @FXML
    private Text emailText;

    @FXML
    private Text phoneText;

    @FXML
    private Text genderText;

    @FXML
    private Button backButton;

    @FXML
    private Button updateProfileButton;

    @FXML
    private Button logoutButton;

    public void initialize() {
        LoggedInUser user = LoggedInUser.getInstance();
        if (user != null) {
            nameText.setText(user.getName());
            emailText.setText(user.getEmail());
            phoneText.setText(user.getPhone());
            genderText.setText(user.getGender());
        }
    }

    @FXML
    protected void handleBackButtonClick() {
        switchToScene("/com/example/rideease/dashboard.fxml");  // Replace with actual path to your dashboard
    }

    @FXML
    protected void handleUpdateProfileButtonClick() {
        switchToScene("/com/example/rideease/updateprofile.fxml");  // Replace with actual path to your update profile page
    }

    @FXML
    protected void handleLogoutButtonClick() {
        switchToScene("/com/example/rideease/scene1.fxml");  // Replace with actual path to your scene1.fxml
    }

    private void switchToScene(String fxmlFile) {
        try {
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), fxmlFile);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the scene.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
