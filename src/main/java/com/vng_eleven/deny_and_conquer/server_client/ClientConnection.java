package com.vng_eleven.deny_and_conquer.server_client;

import java.net.ServerSocket;
import java.net.Socket;

public class ClientConnection implements Runnable{
    Server server;
    Socket client;

    ClientConnection(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            client = server.getServerSocket().accept();
            System.out.println("Connected to client!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
