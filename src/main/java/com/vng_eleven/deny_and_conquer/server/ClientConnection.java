package com.vng_eleven.deny_and_conquer.server;

import javax.crypto.MacSpi;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

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
            int dimension = server.dimension;
            sendMessage(new TokenMessage(TokenMessage.Token.SIZE, dimension, dimension, dimension));

            // upon successful connection set up
            server.addConnection(this);

            boolean isListening = true;
            while (isListening) {
                TokenMessage msg = (TokenMessage) this.is.readObject();
                if (msg.isEndGameMessage()) {
                    isListening = false;
                }
                else {
                    server.enqueue(msg);
                }
            }
            is.close();
            os.close();
            client.close();
        }
        catch (SocketException e) {
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(TokenMessage msg) {
        try {
            os.writeObject(msg);
            os.flush();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
