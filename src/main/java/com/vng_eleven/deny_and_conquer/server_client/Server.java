package com.vng_eleven.deny_and_conquer.server_client;

import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// server has a central board and processes requests from clients
public class Server extends Thread{
    private static final int MAX_NUMBER_OF_PLAYERS = 5;
    public static final int DEFAULT_PORT = 7777;
    private static final Server serverInstance = new Server();

    // server distributes pen colors
    private static final int[] colors = {0xFF0000, 0xFF00, 0xFF, 0xFFFF, 0x7D00FF};

    ServerSocket server;
    String ipAddress;
    List<ClientConnection> clientThreads;

    Queue<TokenMessage> msgQueue;

    Server() {
        try {
            server = new ServerSocket(DEFAULT_PORT);
            ipAddress = InetAddress.getLocalHost().getHostAddress();
            msgQueue = new LinkedList<>();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        clientThreads = new ArrayList<>();
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // main server workflow
    @Override
    public void run() {
        try {
            establishPlayerConnections();

            // dummy code
            while (true) {
                if (!msgQueue.isEmpty()) {
                    System.out.println("Server: processing message");
                    TokenMessage msg = msgQueue.remove();
                    System.out.println(msg);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void establishPlayerConnections() throws InterruptedException {
        // establish connection to all players
        for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
            ClientConnection cc = new ClientConnection(getInstance());
            cc.start();
        }
        synchronized (this) {
            wait(3 * 60 * 1000);
        }
        System.out.println("Done waiting " + clientThreads);

        for (int i = 0; i < clientThreads.size(); i++) {
            clientThreads.get(i).sendMessage(new TokenMessage(TokenMessage.Token.START_GAME, colors[i]));
            System.out.println("Start message sent to client " + i);
        }
    }

    public synchronized void enqueue(TokenMessage msg) {
        msgQueue.add(msg);
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

    public static Server getInstance() {
        return serverInstance;
    }
}
