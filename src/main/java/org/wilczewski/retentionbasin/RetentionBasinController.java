package org.wilczewski.retentionbasin;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RetentionBasinController {
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
    public TextField inRiverPortTxtField;
    @FXML
    public TextField inRiverHostTxtField;
    @FXML
    public ProgressBar volumeProgressBar;
    @FXML
    public Label outflowTxtLabel;
    @FXML
    public Label inflowTxtLabel;
    @FXML
    public Label fillingPercentageTxtLabel;
    @FXML
    public TableView<RiversSectionEntryItem> riversSectionTableView;
    @FXML
    public TableColumn<RiversSectionEntryItem, String> hostRiverSectionCollumnView;
    @FXML
    public TableColumn<RiversSectionEntryItem, Integer> portRiverSectionCollumnView;


    RetentionBasinService retentionBasinService;

    @FXML
    private void initialize() {
        hostRiverSectionCollumnView.setCellValueFactory(cellData -> cellData.getValue().hostProperty());
        hostRiverSectionCollumnView.setCellFactory(TextFieldTableCell.forTableColumn());
        hostRiverSectionCollumnView.setOnEditCommit(event -> {
            RiversSectionEntryItem item = event.getRowValue();
            item.setHost(event.getNewValue());
        });

        portRiverSectionCollumnView.setCellValueFactory(cellData -> cellData.getValue().portProperty().asObject());
        portRiverSectionCollumnView.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        portRiverSectionCollumnView.setOnEditCommit(event -> {
            RiversSectionEntryItem item = event.getRowValue();
            item.setPort(event.getNewValue());
        });

        riversSectionTableView.setEditable(true);

        riversSectionTableView.setItems(FXCollections.observableArrayList());
    }




    public void setRetentionBasinService(RetentionBasinService retentionBasinService) {
        this.retentionBasinService = retentionBasinService;
    }

    @FXML
    private void setNetworkConfig(ActionEvent actionEvent) throws IOException {
        try {
            String ownPortStr = ownPortTxtField.getText();
            if(ownPortStr.isEmpty()) throw new IllegalArgumentException("Own port cannot be empty");
            int ownPort = Integer.parseInt(ownPortStr);

            String ownHost = ownHostTxtField.getText();
            if(ownHost.isEmpty()) throw new IllegalArgumentException("Own port cannot be empty");

            String maxVolumeStr = maxVolumeTxtField.getText();
            if(maxVolumeStr.isEmpty()) throw new IllegalArgumentException("Max volume cannot be empty");
            int maxVolume = Integer.parseInt(maxVolumeStr);
            if(maxVolume < 0) throw new IllegalArgumentException("Max volume cannot be negative");

            String centralPortStr = CentralControlPortTxtField.getText();
            if(centralPortStr.isEmpty()) throw new IllegalArgumentException("Central control port cannot be empty");
            int centralPort = Integer.parseInt(centralPortStr);
            if(centralPort < 0) throw new IllegalArgumentException("Central control port cannot be negative");

            String centralHost = CentralControlHostTxtField.getText();
            if(centralHost.isEmpty()) throw new IllegalArgumentException("Central control port cannot be empty");

            Map<Integer, String> inRiverSections = new HashMap<>();
            for (RiversSectionEntryItem entry : riversSectionTableView.getItems()) {
                String host = entry.getHost();
                int port = entry.getPort();

                if (host == null || host.isEmpty()) throw new IllegalArgumentException("River section host cannot be empty");
                if (port <= 0) throw new IllegalArgumentException("River section port must be positive");

                inRiverSections.put(port, host);
            }

            configurationButton.setDisable(true);
            this.retentionBasinService.configuration(maxVolume, ownPort, ownHost, centralPort, centralHost, inRiverSections);
            showMaxVolume(maxVolume);
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private void showMaxVolume(int maxVolume) {
        valueTxtLabel.setText(String.valueOf(maxVolume));
    }

    @FXML
    private void startWorking(ActionEvent actionEvent) throws IOException, InterruptedException {
        retentionBasinService.run();
    }

    public void updateVolume(double fillPercentage) {
        volumeProgressBar.setProgress(fillPercentage);
        fillPercentage *= 100;
        fillingPercentageTxtLabel.setText(String.valueOf(fillPercentage)+"%");
    }

    public void updateFlow(int inflow, int outflow) {
        outflowTxtLabel.setText(String.valueOf(outflow));
        inflowTxtLabel.setText(String.valueOf(inflow));
    }

    @FXML
    private void addRow(ActionEvent actionEvent) {
        String inRiverSectionHost = inRiverHostTxtField.getText();
        if(inRiverSectionHost.isEmpty()) throw new IllegalArgumentException("In river port cannot be empty");

        String inRiverSectionPortStr = inRiverPortTxtField.getText();
        if(inRiverSectionPortStr.isEmpty()) throw new IllegalArgumentException("In river port cannot be empty");
        int inRiverSectionPort = Integer.parseInt(inRiverSectionPortStr);
        if(inRiverSectionPort < 0) throw new IllegalArgumentException("In river port cannot be negative");


        RiversSectionEntryItem newItem = new RiversSectionEntryItem(inRiverSectionHost, inRiverSectionPort);

        inRiverHostTxtField.clear();
        inRiverPortTxtField.clear();

        riversSectionTableView.getItems().add(newItem);
    }


}