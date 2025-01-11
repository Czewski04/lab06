package org.wilczewski.environment;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EnvironmentApp extends Application {
    public static void main(String[] args) throws IOException, InterruptedException {
//        EnvironmentService environmentService = new EnvironmentService(998, "localhost");
//        environmentService.startServer();
//        TimeUnit.SECONDS.sleep(5);
//        environmentService.setRainfall(500, "localhost", 100);
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/EnvironmentAppView.fxml"));
        Parent root = loader.load();

        EnvironmentController controller = loader.getController();
        EnvironmentService environmentService = new EnvironmentService(controller);
        controller.setEnvironmentService(environmentService);

        stage.setTitle("Environment");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
