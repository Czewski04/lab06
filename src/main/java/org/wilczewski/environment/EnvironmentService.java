package org.wilczewski.environment;

import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class EnvironmentService implements IEnvironment{
    private int ownPort;
    private String ownHost;
    private ConcurrentHashMap<Integer, String> riverSectionsMap;
    private EnvironmentController controller;

    public EnvironmentService(EnvironmentController controller) {
        this.controller = controller;
    }

    public void configuration(int ownPort, String ownHost) throws IOException {
        this.ownPort = ownPort;
        this.ownHost = ownHost;
        riverSectionsMap = new ConcurrentHashMap<>();
        startServer();
    }

    public void run(){
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(()->controller.showRivers(riverSectionsMap));
                System.out.println("control center running");
            }
        });
        thread.start();
    }

    @Override
    public void assignRiverSection(int port, String host) {
        riverSectionsMap.put(port, host);
        System.out.println("dodano rzekę do środowika" + host +" " + port);
    }

    public void setRainfall(int port, String host, int rainfall) throws IOException {
        String message = "srf:" + rainfall;
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
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))){
            String request = reader.readLine();
            handleRequest(request);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleRequest(String request){
        System.out.println(request);
        if(request.startsWith("ars:")){
            String[] parts = request.substring(4).split(",");
            int port = Integer.parseInt(parts[0]);
            String host = parts[1];
            assignRiverSection(port, host);
        }
    }
}
