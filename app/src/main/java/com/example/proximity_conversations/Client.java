package com.example.proximity_conversations;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    // should have - socket, inputStream and output stream
    String otherDeviceIP;
    int otherDevicePort;

    Socket socket = null;
    DataInputStream dataInputStream = null;
    DataOutputStream dataOutputStream = null;

    // set IP address and port number of other device in constructor call
    public Client(String IP, int port){
        this.otherDeviceIP = IP;
        this.otherDevicePort = port;
    }

    public void startMessaging(){
        try{
            socket = new Socket(otherDeviceIP, otherDevicePort);
            Log.e("Client", "startMessaging: Connected");

            //set outputStream
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    dataOutputStream.writeUTF(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    public void stopMessaging(){
        // close all the open streams and sockets
        try{
            dataOutputStream.close();
            socket.close();
        }
        catch(IOException i){
            Log.e("stopMessaging: ", i.getMessage());
        }
    }
}
