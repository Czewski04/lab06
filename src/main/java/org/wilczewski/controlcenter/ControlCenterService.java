package org.wilczewski.controlcenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ControlCenterService implements IControlerCenter{
    private ConcurrentHashMap<Integer, String> retentionBasinsMap;
    private int ownPort;
    private String ownHost;

    public ControlCenterService(int ownPort, String ownHost) {
        this.ownPort = ownPort;
        this.ownHost = ownHost;
        retentionBasinsMap = new ConcurrentHashMap<>();
    }

    @Override
    public void assignRetentionBasin(int port, String host) {
        retentionBasinsMap.put(port, host);
        System.out.println("Dodano zbiornik do centrali" + host + " " + port);

    }

    public void getRetentionBasinFillingPercentage(int port, String host) throws IOException {
        String message = "gfp:";
        startClient(host,port,message).thenAccept(System.out::println);
    }

    public void getRetentionBasinWaterDischarge(int port, String host) throws IOException {
        String message = "gwd:";
        startClient(host,port,message).thenAccept(System.out::println);
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

    public void handleRequest(String request){
        if(request.startsWith("arb:")){
            String[] parts = request.substring(4).split(",");
            int port = Integer.parseInt(parts[0]);
            String host = parts[1];
            assignRetentionBasin(port, host);
        }
    }
}
