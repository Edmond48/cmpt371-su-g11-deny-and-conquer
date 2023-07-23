package com.vng_eleven.deny_and_conquer.server_client;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

// server has a central board and processes requests from clients
public class Server extends Thread{
    private static final int MAX_NUMBER_OF_PLAYERS = 5;
    public static final int DEFAULT_PORT = 7777;
    private static final Server serverInstance = new Server();
    private static int[] colors = {0xFF0000, 0xFF00, 0xFF, 0xFFFF, 0x7D00FF};

    ServerSocket server;
    String ipAddress;
    List<ClientConnection> clientThreads;

    Server() {
        try {
            server = new ServerSocket(DEFAULT_PORT);
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        clientThreads = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
                ClientConnection cc = new ClientConnection(getInstance());
                cc.start();
            }
            synchronized (this) {
                wait(1 * 60 * 1000);
            }
            System.out.println("Done waiting" + clientThreads);


            for (int i = 0; i < clientThreads.size(); i++) {
                clientThreads.get(i).sendMessage(new TokenMessage(TokenMessage.Token.START_GAME, colors[i]));
                System.out.println("Start message sent!");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ServerSocket getServerSocket() {
        return this.server;
    }

    public String getServerIPAddress() {
        return ipAddress;
    }

    public synchronized void addConnection(ClientConnection cc) {
        clientThreads.add(cc);
        if (clientThreads.size() == MAX_NUMBER_OF_PLAYERS) {
            stopWaitingForPlayers();
        }
    }
    public synchronized boolean stopWaitingForPlayers() {
        if (clientThreads.size() > 1) {
            this.notify();
            return true;
        }
        return false;
    }

    static public Server getInstance() {
        return serverInstance;
    }
}
