package com.example.proximity_conversations;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    int myPort = 5000;
    Socket socket = null;
    ServerSocket serverSocket = null; // TCP socket
    DataInputStream dataInputStream = null;
    ServerCallback serverCallback;

    // set port if provided in constructor call
    public Server(int port){
        this.myPort = port;
    }

    public void startServer(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // start the server and wait for client to connect
                try{
                    serverSocket = new ServerSocket(myPort);
                    socket = serverSocket.accept();

                    dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                    startReceivingMessages();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public void startReceivingMessages(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String line = "";

                // read messages from client until "end_connection" is received
                while(!line.equals("end_connection")){
                    try{
                        line = dataInputStream.readUTF();

                        // add onMessageReceivedCallback here
                        serverCallback.onMessageReceived(line);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                // if "end_connection" is received
                // close the streams and sockets
                closeConnection();
            }
        });
        thread.start();
    }

    public void closeConnection(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    dataInputStream.close();
                    socket.close();
                    serverSocket.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public interface ServerCallback{
        public void onMessageReceived(String message);
    }

    public void setServerCallback(ServerCallback serverCallback){
        this.serverCallback = serverCallback;
    }
}
