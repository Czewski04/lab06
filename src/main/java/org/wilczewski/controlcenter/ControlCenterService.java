package org.wilczewski.controlcenter;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ControlCenterService implements IControlerCenter{
    private ConcurrentHashMap<Integer, RetentionBasinMapItem> retentionBasinsMap;
    private int ownPort;
    private String ownHost;
    private ControlCenterController controller;

    public ControlCenterService(ControlCenterController controller) {
        this.controller = controller;
    }

    public void configuration(int ownPort, String ownHost) throws IOException {
        this.ownPort = ownPort;
        this.ownHost = ownHost;
        retentionBasinsMap = new ConcurrentHashMap<>();
        startServer();
    }

    public void run() throws IOException, InterruptedException {
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                retentionBasinsMap.forEach((port, mapItem) -> {
                    try {
                        getRetentionBasinFillingPercentage(port, mapItem.getHost());
                        getRetentionBasinWaterDischarge(port, mapItem.getHost());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                Platform.runLater(()->controller.showBasins(retentionBasinsMap));
                System.out.println("control center running");
            }
        });
        thread.start();
    }

    @Override
    public void assignRetentionBasin(int port, String host) throws IOException {
        retentionBasinsMap.put(port, new RetentionBasinMapItem(host));
        getRetentionBasinWaterDischarge(port, host);
        getRetentionBasinFillingPercentage(port, host);

        System.out.println("Dodano zbiornik do centrali" + host + " " + port);

    }

    public void getRetentionBasinFillingPercentage(int port, String host) throws IOException {
        String message = "gfp:";
        startClient(host,port,message).thenAccept(response -> {retentionBasinsMap.get(port).setFillingPercentage(Double.parseDouble(response));});
    }

    public void getRetentionBasinWaterDischarge(int port, String host) throws IOException {
        String message = "gwd:";
        startClient(host,port,message).thenAccept(response -> {retentionBasinsMap.get(port).setWaterDischargeValve(Integer.parseInt(response));});
    }

    public void setRetentionBasinWaterDischarge(int port, String host, int waterDischarge) throws IOException {
        String message = "swd:" + String.valueOf(waterDischarge);
        startClient(host,port,message);
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

    public CompletableFuture<String> startClient(String host, int port, String message) throws IOException {
        CompletableFuture<String> future = new CompletableFuture<>();
        Thread thread = new Thread(() -> {
            try (Socket clientSocket = new Socket(host, port);
                 BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)){
                writer.println(message);
                String response = reader.readLine();
                future.complete(response);
            } catch (IOException e) {
                e.printStackTrace();
                future.completeExceptionally(e);
            }
        });
        thread.start();
        return future;
    }

    public void handleClient(Socket clientSocket){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
            String request = reader.readLine();
            handleRequest(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleRequest(String request) throws IOException {
        if(request.startsWith("arb:")){
            String[] parts = request.substring(4).split(",");
            int port = Integer.parseInt(parts[0]);
            String host = parts[1];
            assignRetentionBasin(port, host);
        }
    }
}
