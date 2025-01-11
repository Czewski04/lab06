package org.wilczewski.riversection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class RiverSectionService implements IRiverSection {
    private float delay;
    private int ownPort;
    private String ownHost;
    private int environmentPort;
    private String environmentHost;
    private int inBasinPort;
    private String inBasinHost;
    private int outBasinPort;
    private String outBasinHost;
    private int waterInflow;
    private int waterOutflow;
    private int rainfall;

    RiverSectionController controller;

    public RiverSectionService(RiverSectionController controller) {
        this.controller = controller;
    }


    public void configuration(float delay, int ownPort, String ownHost, int environmentPort, String environmentBasinHost, int inBasinPort, String inBasinHost) throws IOException {
        this.delay = delay;
        this.ownPort = ownPort;
        this.ownHost = ownHost;
        this.environmentPort = environmentPort;
        this.environmentHost = environmentBasinHost;
        this.inBasinPort = inBasinPort;
        this.inBasinHost = inBasinHost;
        startServer();
    }

    public void run() throws IOException, InterruptedException {
        //sendRiverSectionData(environmentHost, environmentPort);
        sendRiverSectionData(inBasinHost, inBasinPort);
        Thread thread = new Thread(() -> {
            while(true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("basin running");
            }
        });
        thread.start();
    }

    @Override
    public void setRealDischarge(int realDischarge) {
        this.waterInflow = realDischarge;
    }

    @Override
    public void setRainfall(int rainfall) {
        this.rainfall = rainfall;
    }

    @Override
    public void assignsRetentionBasin(int port, String host) {
        this.outBasinHost = host;
        this.outBasinPort = port;
        System.out.println("retention basin assigned");
    }

    public void sendRiverSectionData(String host, int port) throws IOException {
        String message = "ars:"+ownPort+","+ownHost;
        startClient(host, port, message);
    }

    public void sendWaterDischarge() throws IOException {
        String message = "swi:"+waterInflow;
        startClient(outBasinHost, outBasinPort, message);
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
        if(request.startsWith("srd:")){
            int discharge = Integer.parseInt(request.substring(4));
            setRealDischarge(discharge);
        }
        else if(request.startsWith("srf:")){
            int rainfall = Integer.parseInt(request.substring(4));
            setRainfall(rainfall);
        }
        else if(request.startsWith("arb:")){
            String[] parts = request.substring(4).split(",");
            int port = Integer.parseInt(parts[0]);
            String host = parts[1];
            assignsRetentionBasin(port, host);
        }
    }
}
