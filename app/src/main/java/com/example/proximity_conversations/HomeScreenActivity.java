package com.example.proximity_conversations;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeScreenActivity extends AppCompatActivity implements NsdHelper.DiscoveredDeviceCallback{
    private static RecyclerView discoveredDevicesRecyclerView;
    private static DiscoveredDevices_RecyclerViewAdapter discoveredDevicesRecyclerViewAdapter;
    ArrayList<DiscoveredDeviceModel> discoveredDevices = new ArrayList<>();
    private static Context context;
    private static Handler handler;
    private static NsdHelper nsdHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        context = this;
        handler = new Handler();
        nsdHelper = NsdHelper.getNsdHelper(context, handler);

        nsdHelper.setDiscoveredDeviceCallback(this);

        discoveredDevicesRecyclerView = findViewById(R.id.recyclerView_for_discoveredDevices);
        // set adapter for recycler view
        discoveredDevicesRecyclerViewAdapter = new DiscoveredDevices_RecyclerViewAdapter(this, discoveredDevices);
        discoveredDevicesRecyclerView.setAdapter(discoveredDevicesRecyclerViewAdapter);
        // set layout-manager for recycler view
        discoveredDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void addDiscoveredDevice(String username, String IP, int port){
        DiscoveredDeviceModel newDevice = new DiscoveredDeviceModel(username, IP, port);
        if(discoveredDevices.contains(newDevice)){
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // add newly discovered device to the list that our recycler view is using
                discoveredDevices.add(newDevice);
                discoveredDevicesRecyclerViewAdapter .notifyItemInserted(discoveredDevices.size()-1);
                discoveredDevicesRecyclerView.scrollToPosition(discoveredDevices.size()-1);
            }
        });
    }
}