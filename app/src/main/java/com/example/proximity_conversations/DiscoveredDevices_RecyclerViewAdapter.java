package com.example.proximity_conversations;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DiscoveredDevices_RecyclerViewAdapter extends RecyclerView.Adapter<DiscoveredDevices_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<DiscoveredDeviceModel>discoveredDevices;

    public DiscoveredDevices_RecyclerViewAdapter(Context context, ArrayList<DiscoveredDeviceModel>discoveredDevices){
        this.context = context;
        this.discoveredDevices = discoveredDevices;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.discovered_device_recycler_view_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //The ViewHolder is a wrapper around a View that contains the layout for an individual item in the list
        //Log.e("class of holder", holder.getClass().toString()); // --> class com.example.proximity_conversations.Message_RecyclerViewAdapter$MyViewHolder

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.LEFT;
        params.topMargin = 7;
        holder.cardViewForDiscoveredDevice.setLayoutParams(params);

        holder.discoveredDeviceTV.setText(discoveredDevices.get(position).getDiscoveredDeviceUserName());
    }

    @Override
    public int getItemCount() {
        return discoveredDevices.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cardViewForDiscoveredDevice;
        TextView discoveredDeviceTV;
        ImageView discoveredDeviceIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewForDiscoveredDevice = itemView.findViewById(R.id.discovered_device_card_view);
            discoveredDeviceIV = itemView.findViewById(R.id.card_discovered_device_iv);
            discoveredDeviceTV = itemView.findViewById(R.id.card_discovered_device_tv);
        }
    }
}

