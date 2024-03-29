package com.example.proximity_conversations;

import android.media.Image;
import android.provider.MediaStore;

import java.io.File;

public class DiscoveredDeviceModel {
    private String discoveredDeviceIP;
    private int discoveredDevicePort;
    private String discoveredDeviceUserName;
    private Image discoveredDeviceProfileImage;

    public DiscoveredDeviceModel(String username, String IP, int port){//, Image profileImage){
        this.discoveredDeviceUserName = username;
        this.discoveredDeviceIP = IP;
        this.discoveredDevicePort = port;
        //this.discoveredDeviceProfileImage = profileImage;
    }

    public String getDiscoveredDeviceUserName(){
        return discoveredDeviceUserName;
    }

    public String getDiscoveredDeviceIP(){
        return discoveredDeviceIP;
    }

    public int getDiscoveredDevicePort(){
        return discoveredDevicePort;
    }

    public Image getDiscoveredDeviceProfileImage(){
        return discoveredDeviceProfileImage;
    }
}
