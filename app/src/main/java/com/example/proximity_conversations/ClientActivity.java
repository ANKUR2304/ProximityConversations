package com.example.proximity_conversations;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientActivity extends AppCompatActivity implements Client.ClientCallback {
    TextView messageTV;
    String serverIP;
    EditText messageET;
    EditText serverIPEditText;
    Button setServerIPBtn;
    Button sendMessageBtn;
    Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        messageTV = findViewById(R.id.client_message_tv);
        messageET = findViewById(R.id.client_message_et);
        messageET.setVisibility(View.INVISIBLE);

        serverIPEditText = findViewById(R.id.client_ip_et);

        setServerIPBtn = findViewById(R.id.set_server_ip_btn);

        sendMessageBtn = findViewById(R.id.send_message_btn);
        sendMessageBtn.setVisibility(View.INVISIBLE);

        setServerIPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverIP = serverIPEditText.getText().toString();

                serverIPEditText.setVisibility(View.INVISIBLE);
                messageET.setVisibility(View.VISIBLE);
                setServerIPBtn.setVisibility(View.INVISIBLE);

                setupClient();
            }
        });

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.sendMessage(messageET.getText().toString());
                // onClickListener executes on Main Thread
                // And, we can not access network on Main thread

                /* *******************************************************************************
                If we call the network on main thread, we will get exception stating:
                 android.os.StrictMode$StrictModeViolation
                 android.os.StrictMode.executeDeathPenalty
                ****************************************************************************** */
            }
        });
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
                sendMessageBtn.setVisibility(View.VISIBLE);
                Toast.makeText(ClientActivity.this, "connecting to the server", Toast.LENGTH_SHORT).show();
            }
        });
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
}