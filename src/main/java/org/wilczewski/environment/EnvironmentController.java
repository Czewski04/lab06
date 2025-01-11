package org.wilczewski.environment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class EnvironmentController {
    @FXML
    public Button configurationButton;
    @FXML
    public TextField ownHostTxtField;
    @FXML
    public TextField ownPortTxtField;
    @FXML
    public VBox riversVbox;

    private EnvironmentService environmentService;

    public void setEnvironmentService(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @FXML
    public void setNetworkConfig(ActionEvent actionEvent) {
        try {
            String ownPortStr = ownPortTxtField.getText();
            if (ownPortStr.isEmpty()) throw new IllegalArgumentException("Own port cannot be empty");
            int ownPort = Integer.parseInt(ownPortStr);

            String ownHost = ownHostTxtField.getText();
            if (ownHost.isEmpty()) throw new IllegalArgumentException("Own port cannot be empty");

            configurationButton.setDisable(true);
            this.environmentService.configuration(ownPort, ownHost);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void startWorking(ActionEvent actionEvent) throws IOException, InterruptedException {
        environmentService.run();
    }

    public void showRivers(ConcurrentHashMap<Integer, String> riverSectionsMap) {
        riversVbox.getChildren().clear();
        riversVbox.setSpacing(10);
        for (var entry : riverSectionsMap.entrySet()) {
            int riverPort = entry.getKey();
            String riverHost = entry.getValue();

            Label port = new Label("Port: " + riverPort);
            Label hostLabel = new Label("Host: " + riverHost);

            TextField rainfallTxtField = new TextField();
            rainfallTxtField.setPromptText("Generate rainfall");

            Button setButton = new Button("Set");
            setButton.setOnAction(event -> {
                try {
                    int rainfall = Integer.parseInt(rainfallTxtField.getText());
                    environmentService.setRainfall(riverPort, riverHost, rainfall);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            HBox hbox = new HBox(10, port, hostLabel, rainfallTxtField, setButton);
            hbox.setAlignment(Pos.CENTER_RIGHT);

            riversVbox.getChildren().add(hbox);
        }
    }
}
