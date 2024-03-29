package com.example.proximity_conversations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.proximity_conversations.NsdHelper;

import java.net.ServerSocket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private NsdHelper nsdHelper;
    private Handler handler;
    private Context context;

    private static String username = "Proximity User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        handler = new Handler();
        nsdHelper = NsdHelper.getNsdHelper(context, handler);

        EditText usernameEditText = (EditText)findViewById(R.id.username_text_input);
        Button nextButton = findViewById(R.id.main_activity_next_button_id);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                setupNsdService();

                Intent homeScreenActivityIntent = new Intent(context, HomeScreenActivity.class);
                startActivity(homeScreenActivityIntent);
            }
        });
    }

    public void setupNsdService(){
        Thread nsdServiceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // set username
                nsdHelper.setServiceName(username);

                // Register NsdService
                nsdHelper.registerService();

                // Start nsdDiscovery
                nsdHelper.startNsdDiscovery();
            }
        });
        nsdServiceThread.start();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        nsdHelper.tearDown();
    }
}