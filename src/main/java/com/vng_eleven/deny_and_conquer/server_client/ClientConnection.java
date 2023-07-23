package com.vng_eleven.deny_and_conquer.server_client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientConnection extends Thread{
    Server server;
    Socket client;
    ObjectInputStream is;
    ObjectOutputStream os;

    ClientConnection(Server server) {
        this.server = server;
    }

    @Override
    public void run() {
        try {
            this.client = server.getServerSocket().accept();

            // need to create output stream first before input and flush it
            // due to a header in the stream
            // can cause deadlock
            this.os = new ObjectOutputStream(client.getOutputStream());
            os.flush();
            this.is = new ObjectInputStream(client.getInputStream());

            // upon successful connection set up
            server.addConnection(this);
            System.out.println("Connection to client accepted");

            while (true) {
                TokenMessage msg = (TokenMessage) this.is.readObject();
                server.enqueue(msg);
                synchronized (this) { wait(10); }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendMessage(TokenMessage msg) {
        try {
            os.writeObject(msg);
            os.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
