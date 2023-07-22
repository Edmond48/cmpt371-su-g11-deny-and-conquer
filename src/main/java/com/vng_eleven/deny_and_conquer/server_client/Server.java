package com.vng_eleven.deny_and_conquer.server_client;

import java.net.*;
import java.util.ArrayList;
import java.util.List;

// server has a central board and processes requests from clients
public class Server extends Thread{
    private static final int MAX_NUMBER_OF_PLAYERS = 5;
    public static final int DEFAULT_PORT = 7777;
    private static final Server serverInstance = new Server();

    ServerSocket server;
    String ipAddress;
    List<Thread> clientThreads;

    Server() {
        try {
            server = new ServerSocket(DEFAULT_PORT);
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        clientThreads = new ArrayList<Thread>();
    }

    @Override
    public void run() {
        boolean waitingForPlayers = true;
        System.out.println("Waiting for players...");
        try {
            while (waitingForPlayers) {
                Thread thread = new Thread(new ClientConnection(this));
                thread.start();
                clientThreads.add(thread);

                // hack
                if (clientThreads.size() == MAX_NUMBER_OF_PLAYERS) {
                    waitingForPlayers = false;
                }
            }
            sleep(1000);
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

    static public Server getInstance() {
        return serverInstance;
    }
}
