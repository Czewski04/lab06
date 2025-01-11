package org.wilczewski.retentionbasin;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
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
    private int waterInflow;
    private double fillingPercentage;
    private String outRiverSectionHost;
    private int outRiverSectionPort;
    private ConcurrentHashMap<Integer, Integer> inRiverSections;
    private RetnetionBasinController controller;

    public RetentionBasinService(RetnetionBasinController controller) {
        this.controller = controller;
    }

    public void configuration(int volume, int ownPort, String ownHost, int centralPort, String centralHost, int outRiverSectionPort, String outRiverSectionHost) throws IOException, InterruptedException {
        this.maxVolume = volume;
        this.ownPort = ownPort;
        this.ownHost = ownHost;
        this.centralPort = centralPort;
        this.centralHost = centralHost;
        this.outRiverSectionHost = outRiverSectionHost;
        this.outRiverSectionPort = outRiverSectionPort;
        this.inRiverSections = new ConcurrentHashMap<>();
        this.volume = 400;
        this.waterDischarge = 30;
        startServer();
    }

    public void run() throws IOException, InterruptedException {
        sendRetentionBasinData(centralHost, centralPort);
        //sendRetentionBasinData(outRiverSectionHost, outRiverSectionPort);
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//                sendWaterDischarge();
//                updateFillingPercentageBar();
                System.out.println("basin running");
            }
        });
        thread.start();
    }

    public void updateFillingPercentageBar(){
        Thread updateSimulationView = new Thread(() -> {
            Platform.runLater(()-> controller.updateVolume(fillingPercentage));
        });
        updateSimulationView.setDaemon(true);
        updateSimulationView.start();
    }

    @Override
    public int getWaterDischarge() {
        return waterDischarge;
    }

    @Override
    public double getFillingPercentage() {
        fillingPercentage = (double)volume/(double)maxVolume;
        System.out.println(volume);
        System.out.println(maxVolume);
        System.out.println(fillingPercentage);
        return fillingPercentage;
    }

    @Override
    public void setWaterDischarge(int waterDischarge) {
        this.waterDischarge = waterDischarge;
        this.waterInflow -= waterDischarge;
    }

    @Override
    public void setWaterInflow(int waterInflow, int port) {
        inRiverSections.put(port, waterInflow);
        this.waterInflow += waterInflow;
    }

    @Override
    public void assignRiverSection(int port, String host) {
        this.outRiverSectionHost = host;
        this.outRiverSectionPort = port;
        System.out.println("river section assigned");
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
        System.out.println(request);
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
