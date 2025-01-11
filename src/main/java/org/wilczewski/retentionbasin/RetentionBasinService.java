package org.wilczewski.retentionbasin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class RetentionBasinService implements IRetentionBasin{
    private int maxVolume;
    private int volume;
    private int ownPort;
    private String ownHost;
    private int centralPort;
    private String centralHost;
    private int waterDischarge;
    private int waterInflow;
    private long fillingPercentage;
    private String outRiverSectionHost;
    private int outRiverSectionPort;
    private ConcurrentHashMap<Integer, Integer> inRiverSections;

    public RetentionBasinService(int volume, int ownPort, String ownHost, int centralPort, String centralHost, int outRiverSectionPort, String outRiverSectionHost) throws IOException {
        this.volume = volume;
        this.ownPort = ownPort;
        this.ownHost = ownHost;
        this.centralPort = centralPort;
        this.centralHost = centralHost;
        this.outRiverSectionHost = outRiverSectionHost;
        this.outRiverSectionPort = outRiverSectionPort;
        this.inRiverSections = new ConcurrentHashMap<>();
        startServer();
    }

    public void run(){
        while(true){

        }
    }

    @Override
    public int getWaterDischarge() {
        return waterDischarge;
    }

    @Override
    public long getFillingPercentage() {
        fillingPercentage = (long)(volume/maxVolume);
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
        return "Error";
    }

}
