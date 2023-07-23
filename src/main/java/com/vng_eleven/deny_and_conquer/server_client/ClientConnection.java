package com.vng_eleven.deny_and_conquer.server_client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends Thread{
    Server server;
    Socket client;

    ClientConnection(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            client = server.getServerSocket().accept();

            // upon successful connection set up
            server.addConnection(this);
        }
        catch (Exception e) {
            System.out.println("Thread did not set up connection");
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(TokenMessage msg) {
        try {
            ObjectOutputStream oStream = new ObjectOutputStream(client.getOutputStream());
            oStream.writeObject(msg);
            oStream.flush();
            oStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
