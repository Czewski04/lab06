package org.wilczewski.riversection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RiverSectionApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RiverSectionAppView.fxml"));
        Parent root = loader.load();

        RiverSectionController controller = loader.getController();
        RiverSectionService riverSectionService = new RiverSectionService(controller);
        controller.setRiverSectionService(riverSectionService);

        stage.setTitle("River Section");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
