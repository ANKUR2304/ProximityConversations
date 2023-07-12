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
        //The ViewHolder is a wrapper around a View that contains the layout for an individual item in the list
        //Log.e("class of holder", holder.getClass().toString()); // --> class com.example.proximity_conversations.Message_RecyclerViewAdapter$MyViewHolder

        int messageCategory = messages.get(position).getMessageCategory();
        Log.e("onBindViewHolder: ", "" + messageCategory);

        if(messageCategory == 0){//i.e. message is sent
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            params.topMargin = 7;
            holder.cardViewForMessage.setLayoutParams(params);

            holder.messageTV.setText(messages.get(position).getMessage().toString());
        }
        else{
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            params.topMargin = 7;
            holder.cardViewForMessage.setLayoutParams(params);

            holder.messageTV.setText(messages.get(position).getMessage().toString());
            //holder.messageIV.setImageBitmap((Bitmap) messages.get(position).getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        CardView cardViewForMessage;
        TextView messageTV;
        ImageView messageIV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewForMessage = itemView.findViewById(R.id.message_card_view);
            messageIV = itemView.findViewById(R.id.card_message_iv);
            messageTV = itemView.findViewById(R.id.card_message_tv);
        }
    }
}
