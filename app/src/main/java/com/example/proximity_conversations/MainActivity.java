package com.example.proximity_conversations;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import com.example.proximity_conversations.NsdHelper;

import java.net.ServerSocket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    private NsdHelper nsdHelper;
    private Handler handler;
    private Context context;

    private ArrayList<Uri> selectedUris = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        handler = new Handler();
        nsdHelper = new NsdHelper(context, handler);

        setupNsdService();
    }

    public void setupNsdService(){
        Thread nsdServiceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Register NsdService
                nsdHelper.registerService();
                // Start nsdDiscovery
                nsdHelper.startNsdDiscovery();
            }
        });
        nsdServiceThread.start();
    }
}