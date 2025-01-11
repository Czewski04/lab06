package org.wilczewski.riversection;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RiverSectionApp extends Application {
    public static void main(String[] args) throws IOException, InterruptedException {
//        RiverSectionService river = new RiverSectionService(7, 500, "localhost");
//        river.startServer();
//        TimeUnit.SECONDS.sleep(5);
//        river.sendRiverSectionData("localhost", 400);
//        river.sendRiverSectionData("localhost", 998);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RiverSectionAppView.fxml"));
        Parent root = loader.load();

        stage.setTitle("River Section");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
