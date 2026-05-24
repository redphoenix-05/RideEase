package com.example.rideease;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;

import java.io.IOException;

public final class UiSceneFactory {

    private UiSceneFactory() {
    }

    public static Scene loadResponsiveScene(Class<?> anchorClass, String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(anchorClass.getResource(resourcePath));
        return createResponsiveScene(loader.load());
    }

    public static Scene createResponsiveScene(Parent root) {
        return new Scene(wrapResponsiveRoot(root));
    }

    private static Parent wrapResponsiveRoot(Parent root) {
        if (!(root instanceof Region region)) {
            return root;
        }

        double designWidth = region.getPrefWidth();
        double designHeight = region.getPrefHeight();
        if (designWidth <= 0 || designHeight <= 0) {
            return root;
        }

        region.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        region.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        region.setPrefSize(designWidth, designHeight);

        StackPane wrapper = new StackPane(root);
        StackPane.setAlignment(root, Pos.TOP_LEFT);
        wrapper.setStyle("-fx-background-color: #0b1020;");

        Scale scale = new Scale(1.0, 1.0);
        root.getTransforms().add(scale);

        Runnable updateScale = () -> {
            double width = wrapper.getWidth();
            double height = wrapper.getHeight();
            if (width <= 0 || height <= 0) {
                return;
            }

            double factor = Math.min(width / designWidth, height / designHeight);
            scale.setX(factor);
            scale.setY(factor);
            root.setTranslateX((width - designWidth * factor) / 2.0);
            root.setTranslateY((height - designHeight * factor) / 2.0);
        };

        wrapper.widthProperty().addListener((obs, oldValue, newValue) -> updateScale.run());
        wrapper.heightProperty().addListener((obs, oldValue, newValue) -> updateScale.run());
        Platform.runLater(updateScale);

        return wrapper;
    }
}