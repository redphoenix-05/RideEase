package com.example.rideease;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class Scene1Controller {

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    protected void handleLogin() {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), "/com/example/rideease/login.fxml");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleSignUp() {
        try {
            Stage stage = (Stage) signupButton.getScene().getWindow();
            Scene scene = UiSceneFactory.loadResponsiveScene(getClass(), "/com/example/rideease/signup.fxml");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
