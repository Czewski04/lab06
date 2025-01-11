package org.wilczewski.retentionbasin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RetentionBasinApp extends Application {
    public static void main(String[] args) throws IOException, InterruptedException {
        launch(args);
//        RetentionBasinService rBasin = new RetentionBasinService(1000, 400, "localhost");
//        rBasin.startServer();
//        TimeUnit.SECONDS.sleep(5);
//        rBasin.sendRetentionBasinData("localhost", 500);
//        rBasin.sendRetentionBasinData("localhost", 999);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/RetentionBasinAppView.fxml"));
        Parent root = loader.load();

        stage.setTitle("Retention Basin");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
