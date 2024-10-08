package com.example.proximity_conversations;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import android.os.Handler;

public class NsdHelper {
    private static final String TAG = "NsdHelper";
    private static final String SERVICE_TYPE = "_ProximityConversations._tcp";
    private static String SERVICE_NAME = "Proximity Service";

    private Context context;
    private NsdManager nsdManager;

    private Handler handler;

    private ServerSocket serverSocket;
    private int localPort;
    NsdManager.RegistrationListener registrationListener;
    NsdManager.DiscoveryListener discoveryListener;
    NsdManager.ResolveListener resolveListener;
    private static String serviceName;
    NsdServiceInfo mService;

    DiscoveredDeviceCallback discoveredDeviceCallback;

    private static NsdHelper nsdHelper;

    private NsdHelper(Context context, Handler handler){
        this.context = context;
        this.handler = handler;
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    public static NsdHelper getNsdHelper(Context context, Handler handler){
        if(nsdHelper == null){
            nsdHelper = new NsdHelper(context, handler);
        }
        return nsdHelper;
    }

    public void setServiceName(String username){
        SERVICE_NAME = username;
    }

    public void initializeServerSocket(){
        // initializes a server socket on the next available port
        try{
            serverSocket = new ServerSocket(0);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        // Store the chosen port
        localPort = serverSocket.getLocalPort();
    }

    public void registerService() {
        if(registrationListener != null){
            return;
        }

        initializeServerSocket();

        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName(SERVICE_NAME);
        serviceInfo.setServiceType(SERVICE_TYPE);
        serviceInfo.setPort(localPort);

        initializeRegistrationListener();
        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
    }

    public void initializeRegistrationListener() {
        if(registrationListener != null){
            return;
        }
        registrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                serviceName = NsdServiceInfo.getServiceName();
                Log.e("NsdRegistration : ", "Successful!");
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed! Put debugging code here to determine why.
                Log.e("NsdRegistration : ", "Failed!");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.e("NsdUnRegistration : ", "Successful!");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed. Put debugging code here to determine why.
                Log.e("NsdUnRegistration : ", "Failed!");
            }
        };
    }

    // Discovery
    public void startNsdDiscovery(){
        if(discoveryListener != null){
            return;
        }

        initializeDiscoveryListener();
        nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void initializeDiscoveryListener() {
        if(discoveryListener != null){
            return;
        }
        // Instantiate a new DiscoveryListener
        discoveryListener = new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                String foundServiceType = service.getServiceType();
                Log.e(TAG, "found service type = " + foundServiceType);
                Log.e(TAG, "found service name = " + service.getServiceName());

                if (foundServiceType != null && foundServiceType.length() > 0 && foundServiceType.charAt(foundServiceType.length() - 1) == '.') {
                    foundServiceType = foundServiceType.substring(0, foundServiceType.length() - 1);
                }
                Log.d(TAG, "Service discovery success " + service.toString());
                if (!foundServiceType.equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(TAG, "Unknown Service Type: " + foundServiceType + " while my service type is : " + SERVICE_TYPE);
                } else if (service.getServiceName().equals(SERVICE_NAME)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(TAG, "Same machine: " + service);
                } else {
                    initializeResolveListener();
                    Log.e(TAG, "resolving service : " + service);
                    nsdManager.resolveService(service, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }
        };
    }


    // Resolve
    public void initializeResolveListener() {
        if(resolveListener  != null){
            return;
        }
        resolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(TAG, "Resolve Succeeded. " + serviceInfo);

                if (serviceInfo.getServiceName().equals(SERVICE_NAME)) {
                    Log.d(TAG, "Same IP " + SERVICE_NAME + ", therefore, not adding to discovered devices list");
                    return;
                }

                //mService = serviceInfo;
                //localPort = mService.getPort();
                //InetAddress host = mService.getHost();

                // Add this device to discovered list, now
                discoveredDeviceCallback.addDiscoveredDevice(serviceInfo.getServiceName(), serviceInfo.getHost().toString(), serviceInfo.getPort());
            }
        };
    }

    // NsdHelper's tearDown method
    public void tearDown() {
        if(registrationListener != null){
            nsdManager.unregisterService(registrationListener);
            registrationListener = null;
        }
        if(discoveryListener != null){
            nsdManager.stopServiceDiscovery(discoveryListener);
            discoveryListener = null;
        }
        resolveListener = null;
    }

    public interface DiscoveredDeviceCallback{
        public void addDiscoveredDevice(String username, String IP, int port);
    }

    public void setDiscoveredDeviceCallback(DiscoveredDeviceCallback discoveredDeviceCallback){
        this.discoveredDeviceCallback = discoveredDeviceCallback;
    }
}
