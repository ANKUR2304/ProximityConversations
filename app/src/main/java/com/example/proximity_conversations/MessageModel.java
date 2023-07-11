package com.example.proximity_conversations;

import android.media.Image;
import android.provider.MediaStore;

import java.io.File;

public class MessageModel {
    private int messageType = 0; // by default it is 0 i.e. textMessage
    //messageCategory --> 0 for sent, 1 for received
    private int messageCategory = 0; // sent message by default
    private String textMessage;
    private Image imageMessage;
    private MediaStore.Audio audioMessage;
    private MediaStore.Video videoMessage;
    private File documentMessage;

    public MessageModel(String textMessage){
        this.textMessage = textMessage;
        messageType = 0;
        messageCategory = 0;
    }

    public MessageModel(Image imageMessage){
        this.imageMessage = imageMessage;
        messageType = 1;
        messageCategory = 1;
    }

    public MessageModel(MediaStore.Audio audioMessage){
        this.audioMessage = audioMessage;
        messageType = 2;
        messageCategory = 1;
    }

    public MessageModel(MediaStore.Video videoMessage){
        this.videoMessage = videoMessage;
        messageType = 3;
        messageCategory = 1;
    }

    public MessageModel(File documentMessage){
        this.documentMessage = documentMessage;
        messageType = 4;
        messageCategory = 1;
    }

    public int getMessageType(){
        return this.messageType;
    }
    public int getMessageCategory(){
        return this.messageCategory;
    }

    public Object getMessage(){
        if(messageType == 0)return textMessage;
        else if(messageType == 1)return imageMessage;
        else if(messageType == 2)return audioMessage;
        else if(messageType == 3)return videoMessage;
        return documentMessage;
    }
}
