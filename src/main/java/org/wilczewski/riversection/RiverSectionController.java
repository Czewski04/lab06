package org.wilczewski.riversection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;
import org.wilczewski.retentionbasin.RetentionBasinService;

import java.io.IOException;

public class RiverSectionController {
    @FXML
    public Button configurationButton;
    @FXML
    public TextField ownHostTxtField;
    @FXML
    public TextField ownPortTxtField;
    @FXML
    public TextField riverDelayTxtField;
    @FXML
    public TextField environmentHostTxtField;
    @FXML
    public TextField environmentPortTxtField;
    @FXML
    public TextField inBasinPortTxtField;
    @FXML
    public TextField inBasinHostTxtField;
    @FXML
    public Rectangle activeRiverSign;
    @FXML
    public Label rainfallTxtLabel;
    @FXML
    public Label inflowWaterTxtLabel;


    RiverSectionService riverSectionService;

    public void setRiverSectionService(RiverSectionService riverSectionService) {
        this.riverSectionService = riverSectionService;
    }

    @FXML
    private void setNetworkConfig(ActionEvent actionEvent) throws IOException {
        try {
            String ownPortStr = ownPortTxtField.getText();
            if(ownPortStr.isEmpty()) throw new IllegalArgumentException("Own port cannot be empty");
            int ownPort = Integer.parseInt(ownPortStr);

            String ownHost = ownHostTxtField.getText();
            if(ownHost.isEmpty()) throw new IllegalArgumentException("Own port cannot be empty");

            String delayStr = riverDelayTxtField.getText();
            if(delayStr.isEmpty()) throw new IllegalArgumentException("Max volume cannot be empty");
            int riverDelay = Integer.parseInt(delayStr);
            if(riverDelay < 0) throw new IllegalArgumentException("Max volume cannot be negative");

            String environmentPortStr = environmentPortTxtField.getText();
            if(environmentPortStr.isEmpty()) throw new IllegalArgumentException("Central control port cannot be empty");
            int environmentPort = Integer.parseInt(environmentPortStr);
            if(environmentPort < 0) throw new IllegalArgumentException("Central control port cannot be negative");

            String environmentHost = environmentHostTxtField.getText();
            if(environmentHost.isEmpty()) throw new IllegalArgumentException("Central control port cannot be empty");

            String inBasinHost = inBasinHostTxtField.getText();
            if(inBasinHost.isEmpty()) throw new IllegalArgumentException("Out river port cannot be empty");

            String inBasinPortStr = inBasinPortTxtField.getText();
            if(inBasinPortStr.isEmpty()) throw new IllegalArgumentException("Out river port cannot be empty");
            int inBasinPort = Integer.parseInt(inBasinPortStr);
            if(inBasinPort < 0) throw new IllegalArgumentException("Out river port cannot be negative");

            configurationButton.setDisable(true);
            this.riverSectionService.configuration(riverDelay, ownPort, ownHost, environmentPort, environmentHost, inBasinPort, inBasinHost);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startWorking(ActionEvent actionEvent) throws IOException, InterruptedException {
        riverSectionService.run();
    }

    public void showRainfall(int rainfall) {
        rainfallTxtLabel.setText(String.valueOf(rainfall));
    }

    public void showInflowWater(int inflowWater) {
        inflowWaterTxtLabel.setText(String.valueOf(inflowWater));
    }

    public void showActiveRiverSign() {
        activeRiverSign.setVisible(true);
    }
}
