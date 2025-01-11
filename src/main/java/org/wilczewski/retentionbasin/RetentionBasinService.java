package org.wilczewski.retentionbasin;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class RetentionBasinService implements IRetentionBasin{
    private int maxVolume;
    private int volume;
    private int ownPort;
    private String ownHost;
    private int centralPort;
    private String centralHost;
    private int waterDischarge;
    private int realWaterInflow;
    private double fillingPercentage;
    private String outRiverSectionHost;
    private int outRiverSectionPort;
    private ConcurrentHashMap<Integer, Integer> inRiverSectionsWaterInflow;
    private ConcurrentHashMap<Integer, String> inRiverSections;
    private RetentionBasinController controller;

    public RetentionBasinService(RetentionBasinController controller) {
        this.controller = controller;
        this.inRiverSectionsWaterInflow = new ConcurrentHashMap<>();
    }

    public void configuration(int volume, int ownPort, String ownHost, int centralPort, String centralHost, Map<Integer, String> inRiverSections) throws IOException, InterruptedException {
        this.maxVolume = volume;
        this.ownPort = ownPort;
        this.ownHost = ownHost;
        this.centralPort = centralPort;
        this.centralHost = centralHost;
        this.inRiverSections = new ConcurrentHashMap<>(inRiverSections);
        startServer();
    }

    public void run() throws IOException, InterruptedException {
        sendRetentionBasinData(centralHost, centralPort);
        inRiverSections.forEach((port, host) -> {
            try {
                sendRetentionBasinData(host, port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    sendWaterDischarge();
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
                calculateAmountOfWater();
                updateFillingPercentageBar();
                updateFlowDisplay();
            }
        });
        thread.start();
    }

    public void calculateAmountOfWater(){
        realWaterInflow = 0;
        inRiverSectionsWaterInflow.forEach((port, water) -> {
            realWaterInflow += water;
        });
        volume += realWaterInflow - waterDischarge;
        if(volume<0) volume = waterDischarge = 0;
        fillingPercentage = (double)volume/(double)maxVolume;
        if(fillingPercentage >= 1) waterDischarge = realWaterInflow;
    }

    public void updateFlowDisplay(){
        Platform.runLater(()-> controller.updateFlow(realWaterInflow, waterDischarge));
    }

    public void updateFillingPercentageBar(){
        Platform.runLater(()-> controller.updateVolume(fillingPercentage));
    }

    @Override
    public int getWaterDischarge() {
        return waterDischarge;
    }

    @Override
    public double getFillingPercentage() {
        fillingPercentage = (double)volume/(double)maxVolume;
        return fillingPercentage;
    }

    @Override
    public void setWaterDischarge(int waterDischarge) {
        this.waterDischarge = waterDischarge;
    }

    @Override
    public void setWaterInflow(int waterInflow, int port) {
        inRiverSectionsWaterInflow.put(port, waterInflow);
    }

    @Override
    public void assignRiverSection(int port, String host) {
        this.outRiverSectionHost = host;
        this.outRiverSectionPort = port;
    }

    public void sendWaterDischarge() throws IOException {
        String message = "srd:"+waterDischarge;
        startClient(outRiverSectionHost, outRiverSectionPort, message);
    }

    public void sendRetentionBasinData(String host, int port) throws IOException {
        String message = "arb:"+ownPort+","+ownHost;
        startClient(host, port, message);
    }

    public void startServer() throws IOException {
        Thread thread = new Thread(() -> {
            try(ServerSocket serverSocket = new ServerSocket(ownPort)){
                while(true){
                    Socket clientSocket = serverSocket.accept();
                    new Thread(() -> handleClient(clientSocket)).start();
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void startClient(String host, int port, String message) throws IOException {
        Thread thread = new Thread(() -> {
            try (Socket clientSocket = new Socket(host, port);
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)){
                writer.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void handleClient(Socket clientSocket){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)){

            String request = reader.readLine();
            String response = handleRequest(request);
            writer.println(response);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String handleRequest(String request){
        if(request.startsWith("gwd")){
            return String.valueOf(getWaterDischarge());
        }
        else if(request.startsWith("gfp")){
            return String.valueOf(getFillingPercentage());
        }
        else if(request.startsWith("swd:")){
            int discharge = Integer.parseInt(request.substring(4));
            setWaterDischarge(discharge);
        }
        else if(request.startsWith("swi:")){
            String[] parts = request.substring(4).split(",");
            int inflow = Integer.parseInt(parts[0]);
            int port = Integer.parseInt(parts[1]);
            setWaterInflow(inflow, port);
        }
        else if(request.startsWith("ars:")){
            String[] parts = request.substring(4).split(",");
            int port = Integer.parseInt(parts[0]);
            String host = parts[1];
            assignRiverSection(port, host);
        }
        return "ok";
    }

}
