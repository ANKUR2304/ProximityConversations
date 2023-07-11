package com.example.proximity_conversations;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Message_RecyclerViewAdapter extends RecyclerView.Adapter<Message_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    ArrayList<MessageModel>messages;

    public Message_RecyclerViewAdapter(Context context, ArrayList<MessageModel>messages){
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.message_recycler_view_row, parent, false);
        return new Message_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int messageCategory = messages.get(position).getMessageCategory();
        Log.e("onBindViewHolder: ", "" + messageCategory);

        if(messageCategory == 0){
            holder.messageTV.setText(messages.get(position).getMessage().toString());
        }
        else{
            holder.messageIV.setImageBitmap((Bitmap) messages.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView messageTV;
        ImageView messageIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messageIV = itemView.findViewById(R.id.card_message_iv);
            messageTV = itemView.findViewById(R.id.card_message_tv);
        }
    }
}
