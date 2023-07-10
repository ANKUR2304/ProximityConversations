package com.example.proximity_conversations;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerActivity extends AppCompatActivity implements Server.ServerCallback {
    TextView messageTV;
    EditText messageET;
    Button sendMessageBtn;
    TextView ipTV;
    Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        messageTV = findViewById(R.id.server_message_tv);
        messageET = findViewById(R.id.server_message_et);
        ipTV = findViewById(R.id.server_ip_tv);
        sendMessageBtn = findViewById(R.id.send_message_btn);

        messageET.setVisibility(View.INVISIBLE);
        sendMessageBtn.setVisibility(View.INVISIBLE);

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server.sendMessage(messageET.getText().toString());
            }
        });

        // get IP of the server device and show via text view
        Toast.makeText(ServerActivity.this, "fetching IP address of the device", Toast.LENGTH_SHORT).show();

        try{
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            // traverse all the interfaces
            while(interfaces.hasMoreElements()){
                NetworkInterface iface = interfaces.nextElement();
                // filter out 127.0.0.1 and inactive interfaces
                if(iface.isLoopback() || !iface.isUp()){
                    continue;
                }

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                // traverse all formats of addresses
                while(addresses.hasMoreElements()){
                    InetAddress addr = addresses.nextElement();
                    // skip IPv6 format
                    if(addr instanceof Inet6Address)continue;
                    String ip = addr.getHostAddress();
                    ipTV.setText(ip);
                    Log.e("onCreate: ", ip);
                }
            }

            // start server
            server = new Server(5000);
            server.startServer();
            server.setServerCallback((Server.ServerCallback) this);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessageReceived(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageTV.setText(message);
            }
        });
    }

    public void onClientAccepted(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageET.setVisibility(View.VISIBLE);
                sendMessageBtn.setVisibility(View.VISIBLE);
            }
        });
        Log.e("onClientAccepted: ", "done");
    }
}