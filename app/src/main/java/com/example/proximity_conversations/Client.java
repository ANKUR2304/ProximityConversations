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

    ClientCallback clientCallback = null;

    // set IP address and port number of other device in constructor call
    public Client(String IP, int port){
        this.otherDeviceIP = IP;
        this.otherDevicePort = port;
    }

    public void startMessaging(){
        try{
            socket = new Socket(otherDeviceIP, otherDevicePort);
            Log.e("Client", "startMessaging: Connected");

            //set Input Stream and Output Stream
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            startReceivingMessages();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startReceivingMessages(){
        // we need to start receiving messages in a new thread because we cannot block the Main UI thread(that is responsible for handling UI)
        // so, we can run a separate thread to check for new messages
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String line = "";
                // read messages until "end_connection" is received
                while(!line.equals("end_connection")){
                    try{
                        line = dataInputStream.readUTF();
                        clientCallback.onMessageReceived(line);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                // if "end_connection" is received, close all the streams and sockets
                stopMessaging();
            }
        });
        thread.start();
        Log.e("startReceivingMessages: ", "started successfully");
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
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();
        }
        catch(IOException i){
            Log.e("stopMessaging: ", i.getMessage());
        }
    }

    // clientCallback
    public interface ClientCallback{
        public void onMessageReceived(String message);
    }

    public void setClientCallback(ClientCallback clientCallback){
        this.clientCallback = clientCallback;
    }
}
