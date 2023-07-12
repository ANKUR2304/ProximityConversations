package com.example.proximity_conversations;

import static android.view.View.GONE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class ServerActivity extends AppCompatActivity implements Server.ServerCallback {
    EditText messageET;
    ImageView sendMessageIcon;
    TextView ipTV;
    Server server;

    RecyclerView recyclerView;
    Message_RecyclerViewAdapter adapter;
    ArrayList<MessageModel> messages = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        recyclerView = findViewById(R.id.recyclerView_for_messages);
        recyclerView.setVisibility(GONE);

        messageET = findViewById(R.id.server_message_et);
        ipTV = findViewById(R.id.server_ip_tv);
        sendMessageIcon = findViewById(R.id.send_message_icon);

        messageET.setVisibility(View.GONE);
        sendMessageIcon.setVisibility(View.GONE);

        sendMessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageET.getText().toString();
                server.sendMessage(message);
                messageET.getText().clear();

                // show sent message on our side also
                MessageModel messageModel = new MessageModel(message);
                messageModel.setMessageCategory(0);

                messages.add(messageModel);
                adapter.notifyItemInserted(messages.size()-1);
                recyclerView.scrollToPosition(messages.size()-1);
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
            // set adapter for recycler view
            adapter = new Message_RecyclerViewAdapter(this, messages);
            recyclerView.setAdapter(adapter);
            // set layout-manager for recycler view
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
                // add message to messages list that our recycler view is using
                messages.add(new MessageModel(message));
                adapter.notifyItemInserted(messages.size()-1);
                recyclerView.scrollToPosition(messages.size()-1);
            }
        });
    }

    public void onClientAccepted(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ipTV.setVisibility(View.GONE);
                messageET.setVisibility(View.VISIBLE);
                sendMessageIcon.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });
        Log.e("onClientAccepted: ", "done");
    }
}