package org.wilczewski.retentionbasin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class RetnetionBasinController {
    @FXML
    public Label valueTxtLabel;
    @FXML
    public Button configurationButton;
    @FXML
    public TextField maxVolumeTxtField;
    @FXML
    public TextField ownHostTxtField;
    @FXML
    public TextField ownPortTxtField;
    @FXML
    public TextField CentralControlHostTxtField;
    @FXML
    public TextField CentralControlPortTxtField;
    @FXML
    public TextField outRiverPortTxtField;
    @FXML
    public TextField outRiverHostTxtField;

    RetentionBasinService retentionBasinService;

    String ownHost;
    int ownPort;
    int maxVolume;
    int centralPort;
    String centralHost;
    String outRiverSectionHost;
    int outRiverSectionPort;

    @FXML
    public void setNetworkConfig(ActionEvent actionEvent) throws IOException {
        try {
            String ownPortStr = ownPortTxtField.getText();
            if(ownPortStr.isEmpty()) throw new IllegalArgumentException("Own port cannot be empty");
            ownPort = Integer.parseInt(ownPortStr);

            ownHost = ownHostTxtField.getText();
            if(ownHost.isEmpty()) throw new IllegalArgumentException("Own port cannot be empty");

            String maxVolumeStr = maxVolumeTxtField.getText();
            if(maxVolumeStr.isEmpty()) throw new IllegalArgumentException("Max volume cannot be empty");
            maxVolume = Integer.parseInt(maxVolumeStr);
            if(maxVolume < 0) throw new IllegalArgumentException("Max volume cannot be negative");

            String centralPortStr = CentralControlPortTxtField.getText();
            if(centralPortStr.isEmpty()) throw new IllegalArgumentException("Central control port cannot be empty");
            centralPort = Integer.parseInt(centralPortStr);
            if(centralPort < 0) throw new IllegalArgumentException("Central control port cannot be negative");

            centralHost = CentralControlHostTxtField.getText();
            if(centralHost.isEmpty()) throw new IllegalArgumentException("Central control port cannot be empty");

            outRiverSectionHost = outRiverHostTxtField.getText();
            if(outRiverSectionHost.isEmpty()) throw new IllegalArgumentException("Out river port cannot be empty");

            String outRiverSectionPortStr = outRiverPortTxtField.getText();
            if(outRiverSectionPortStr.isEmpty()) throw new IllegalArgumentException("Out river port cannot be empty");
            outRiverSectionPort = Integer.parseInt(outRiverSectionPortStr);
            if(outRiverSectionPort < 0) throw new IllegalArgumentException("Out river port cannot be negative");

            configurationButton.setDisable(true);
            this.retentionBasinService = new RetentionBasinService(maxVolume, ownPort, ownHost, centralPort, centralHost, outRiverSectionPort, outRiverSectionHost);
            showMaxVolume(maxVolume);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

    }

    private void showMaxVolume(int maxVolume) {
        valueTxtLabel.setText(String.valueOf(maxVolume));
    }

    @FXML
    private void startWorking(ActionEvent actionEvent) {
        retentionBasinService.run();
    }

}
