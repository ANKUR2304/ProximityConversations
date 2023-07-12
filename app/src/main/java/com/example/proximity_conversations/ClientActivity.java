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

import java.util.ArrayList;

public class ClientActivity extends AppCompatActivity implements Client.ClientCallback {
    RecyclerView recyclerView;
    Message_RecyclerViewAdapter adapter;
    String serverIP;
    EditText messageET;
    EditText serverIPEditText;
    Button setServerIPBtn;
    ImageView sendMessageIcon;
    Client client;

    ArrayList<MessageModel>messages = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        recyclerView = findViewById(R.id.recyclerView_for_messages);
        recyclerView.setVisibility(GONE);

        messageET = findViewById(R.id.client_message_et);
        messageET.setVisibility(GONE);

        serverIPEditText = findViewById(R.id.client_ip_et);

        setServerIPBtn = findViewById(R.id.set_server_ip_btn);

        sendMessageIcon = findViewById(R.id.send_message_icon);
        sendMessageIcon.setVisibility(GONE);

        setServerIPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverIP = serverIPEditText.getText().toString();

                serverIPEditText.setVisibility(GONE);
                messageET.setVisibility(View.VISIBLE);
                setServerIPBtn.setVisibility(GONE);

                setupClient();
            }
        });

        sendMessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = messageET.getText().toString();
                client.sendMessage(message);
                // onClickListener executes on Main Thread
                // And, we can not access network on Main thread

                /* *******************************************************************************
                If we call the network on main thread, we will get exception stating:
                 android.os.StrictMode$StrictModeViolation
                 android.os.StrictMode.executeDeathPenalty
                ****************************************************************************** */
                messageET.getText().clear();

                // show sent message on our side also
                MessageModel messageModel = new MessageModel(message);
                messageModel.setMessageCategory(0);

                messages.add(messageModel);
                adapter.notifyItemInserted(messages.size()-1);
                recyclerView.scrollToPosition(messages.size()-1);
            }
        });

        // set adapter for recycler view
        adapter = new Message_RecyclerViewAdapter(this, messages);
        recyclerView.setAdapter(adapter);
        // set layout-manager for recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void setupClient(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                client = new Client(serverIP, 5000);
                client.setClientCallback(ClientActivity.this);
                client.startMessaging();
            }
        });
        thread.start();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sendMessageIcon.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(ClientActivity.this, "connecting to the server", Toast.LENGTH_SHORT).show();
            }
        });
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
}