package org.wilczewski.retentionbasin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RetentionBasinApp extends Application {
    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RetentionBasinAppView.fxml"));
        Parent root = loader.load();

        RetentionBasinController controller = loader.getController();
        RetentionBasinService retentionBasinService = new RetentionBasinService(controller);
        controller.setRetentionBasinService(retentionBasinService);

        stage.setTitle("Retention Basin");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
