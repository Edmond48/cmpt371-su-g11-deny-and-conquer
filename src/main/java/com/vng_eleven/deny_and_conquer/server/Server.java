package com.vng_eleven.deny_and_conquer.server;

import com.vng_eleven.deny_and_conquer.client.Board;

import java.net.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// server has a central board and processes requests from clients
public class Server extends Thread{
    public static final int DEFAULT_PORT = 7777;

    private static final int MAX_NUMBER_OF_PLAYERS = 5;
    private static final int PLAYER_WAIT_TIME_IN_MINUTES = 3;

    // server distributes pen colors
    private static final int[] colors = {0xFF0000, 0xFF00, 0xFF, 0xFFFF, 0x7D00FF};
    private final HashMap<Integer, Integer> reverseLookUp;

    // networking components
    ServerSocket server;
    String ipAddress;
    List<ClientConnection> clientThreads;

    // server board
    boolean[][] isLocked;
    int occupiedCells;
    final int totalCells;
    private int[] scores;
    int dimension;

    // safe for concurrent applications
    BlockingQueue<TokenMessage> msgQueue;


    Server() {
        try {
            server = new ServerSocket(DEFAULT_PORT);
            ipAddress = InetAddress.getLocalHost().getHostAddress();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        clientThreads = new ArrayList<>();
        msgQueue = new LinkedBlockingQueue<>();

        dimension = 3;//Board.DEFAULT_DIMENSION;
        isLocked = new boolean[dimension][dimension];
        occupiedCells = 0;
        totalCells = dimension * dimension;
        scores = new int[MAX_NUMBER_OF_PLAYERS];

        // set up reverseLookUp
        this.reverseLookUp = new HashMap<>();
        for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
            reverseLookUp.put(colors[i], i);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // main server workflow
    @Override
    public void run() {
        try {
            establishPlayerConnections();

            // process requests until all cells have been occupied
            while (true) {
                while (msgQueue.size() > 0) {
                    TokenMessage msg = msgQueue.remove();
                    process(msg);
                }
                if (occupiedCells >= totalCells) {
                    announceResult();
                    break;
                }
            }

            // end game
            TokenMessage end = new TokenMessage(TokenMessage.Token.END_GAME, -1, -1, -1);
            broadcast(end);
            // close all connections
            boolean waitingForThreadsToClose = true;
            while (waitingForThreadsToClose) {
                for (ClientConnection cc : clientThreads) {
                    if (cc.isAlive()) {
                        break;
                    }
                }
                waitingForThreadsToClose = false;
            }
            server.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void establishPlayerConnections() throws InterruptedException {
        // open the maximum number of connections
        for (int i = 0; i < MAX_NUMBER_OF_PLAYERS; i++) {
            ClientConnection cc = new ClientConnection(getInstance());
            cc.start();
        }
        // after this wait, 2-5 players are connected
        synchronized (this) {
            wait(minuteToMilli(PLAYER_WAIT_TIME_IN_MINUTES));
        }

        // start game for all connected players
        for (int i = 0; i < clientThreads.size(); i++) {
            clientThreads.get(i).sendMessage(new TokenMessage(TokenMessage.Token.START_GAME, colors[i], dimension, dimension));
        }
    }
    private void process(TokenMessage message) {
        switch (message.getToken()) {
            case ATTEMPT:
                isLocked[message.getRow()][message.getCol()] = true;
                broadcast(message);
                break;
            case RELEASE:
                isLocked[message.getRow()][message.getCol()] = false;
                broadcast(message);
                break;
            case OCCUPY:
                isLocked[message.getRow()][message.getCol()] = true;
                // bookkeeping
                scores[reverseLookUp.get(message.getColor())]++;
                occupiedCells++;
                broadcast(message);
                break;
            default:
                break;
        }
    }
    private void announceResult() {
        int[] rank = new int[MAX_NUMBER_OF_PLAYERS];
        // compute the ranking, can be O(n^2) because the ranks are limited (<=5) anyways
        for (int i = 0; i < scores.length-1; i++) {
            for (int j = i+1; j < scores.length; j++) {
                if (scores[i] < scores[j]) {
                    rank[i]++;
                }
                else if (scores[i] > scores[j]){
                    rank[j]++;
                }
            }
        }
        for (int i = 0; i < scores.length; i++) {
            TokenMessage resultMsg = new TokenMessage(TokenMessage.Token.RESULT, colors[i], scores[i], rank[i]);
            broadcast(resultMsg);
        }
    }
    private void broadcast(TokenMessage msg) {
        for (ClientConnection cc : clientThreads) {
            cc.sendMessage(msg);
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

    // singleton instance
    private static final Server serverInstance = new Server();
    public static Server getInstance() {
        return serverInstance;
    }

    private static int minuteToMilli(int minute) {
        return minute * 60000;
    }
}
