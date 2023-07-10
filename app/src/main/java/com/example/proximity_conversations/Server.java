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
    DataOutputStream dataOutputStream = null;
    ServerCallback serverCallback;

    // set port if provided in constructor call
    public Server(int port){
        this.myPort = port;
    }

    public void startServer(){
        // since the server needs to listen and accept - if any client connects to it
        // therefore,we can not start server in Main/UI thread (avoid blocking Main/UI thread)
        // once, one device has become server and other device has become client, both way communication should be possible
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // start the server and wait for client to connect
                try{
                    serverSocket = new ServerSocket(myPort); // i.e. an endpoint is created that is listening at this particular port
                    socket = serverSocket.accept(); // listens for a connection to be made to this socket and accepts it
                    // accept() method blocks until a connection is made
                    serverCallback.onClientAccepted();
                    // get inputStream from the socket to make dataInputStream - to be able to get the input from client
                    dataInputStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                    // get outputStream from the socket to send data to client
                    dataOutputStream = new DataOutputStream(socket.getOutputStream());
                    
                    // start receiving messages in a new thread
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
    
    public void sendMessage(String message){
        // send message can be called only when there is a socket connection already
        // and that is already handled by logic
        // therefore, no need to worry about socket connection at all
        
        // run logic of sendMessade() in a separate thread, because, again we can not block Main/UI thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataOutputStream.writeUTF(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
        public void onClientAccepted();
    }

    public void setServerCallback(ServerCallback serverCallback){
        this.serverCallback = serverCallback;
    }
}
