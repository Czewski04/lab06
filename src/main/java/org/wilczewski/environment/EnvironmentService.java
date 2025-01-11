package org.wilczewski.environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class EnvironmentService implements IEnvironment{
    private int ownPort;
    private String ownHost;
    private ConcurrentHashMap<Integer, String> riverSectionsMap;

    public EnvironmentService(int ownPort, String ownHost) {
        this.ownPort = ownPort;
        this.ownHost = ownHost;
        riverSectionsMap = new ConcurrentHashMap<>();
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
