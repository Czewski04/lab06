package org.wilczewski.controlcenter;

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

public class ControlCenterController {
    @FXML
    public Button configurationButton;
    @FXML
    public TextField ownHostTxtField;
    @FXML
    public TextField ownPortTxtField;
    @FXML
    public VBox basinsVbox;

    private ControlCenterService controlCenterService;

    public void setControlCenterService(ControlCenterService controlCenterService) {
        this.controlCenterService = controlCenterService;
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
            this.controlCenterService.configuration(ownPort, ownHost);
        }
        catch (IllegalArgumentException e) {
                e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void startWorking(ActionEvent actionEvent) throws IOException, InterruptedException {
        controlCenterService.run();
    }

    public void showBasins(ConcurrentHashMap<Integer, RetentionBasinMapItem> retentionBasinsMap) {
        basinsVbox.getChildren().clear();
        basinsVbox.setSpacing(10);
        for (var entry : retentionBasinsMap.entrySet()) {
            int basinPort = entry.getKey();
            String basinHost = entry.getValue().getHost();
            double basinFillingPercentage = entry.getValue().getFillingPercentage();
            int basinWaterDischarge = entry.getValue().getWaterDischargeValve();

            Label port = new Label("Port: " + basinPort);
            Label hostLabel = new Label("Host: " + basinHost);
            Label fillingPercentageLabel = new Label("Filling: " + String.format("%.2f", basinFillingPercentage*100) + "%");
            Label waterDischargeLabel = new Label("Water Discharge: " + basinWaterDischarge);

            TextField waterDischargeTextField = new TextField();
            waterDischargeTextField.setPromptText("New Water Discharge");

            Button setButton = new Button("Set");
            setButton.setOnAction(event -> {
                try {
                    int discharge = Integer.parseInt(waterDischargeTextField.getText());
                    controlCenterService.setRetentionBasinWaterDischarge(basinPort, basinHost, discharge);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            HBox hbox = new HBox(10, port, hostLabel, fillingPercentageLabel, waterDischargeLabel, waterDischargeTextField, setButton);
            hbox.setAlignment(Pos.CENTER_RIGHT);

            basinsVbox.getChildren().add(hbox);
        }
    }


}
