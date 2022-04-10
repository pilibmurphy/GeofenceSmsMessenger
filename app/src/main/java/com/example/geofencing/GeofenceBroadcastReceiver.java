package com.example.geofencing;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;
import android.telephony.SmsManager;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import androidx.core.content.ContextCompat;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "bcReceiver is working");

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }
        Location location = geofencingEvent.getTriggeringLocation();

        //todo reintroduce different transition types
        //int transitionType = geofencingEvent.getGeofenceTransition();
/*
        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_ENTER", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_DWELL", "", MapsActivity.class);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("GEOFENCE_TRANSITION_EXIT", "", MapsActivity.class);
                break;
        }*/


        notificationHelper.sendHighPriorityNotification("Attempting to send Text Message", "", MapsActivity.class);
        SmsManager smsManager = SmsManager.getDefault();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(context, "Sending Text message", Toast.LENGTH_SHORT).show();
            //smsManager.sendTextMessage("+123456789", null, "hello", null, null);
            //sendText();
        }
    }

    private void sendText(Context context, int RequestId){
        //todo hardcoded string, create input and store on file
        //  get the requestID that will be on file and get the related number and message

        String phoneNo = "+123456789";
        String messageText = "in dublin now";
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, messageText, null, null);
            Toast.makeText(context, "SMS Sent Successfully!",
                    Toast.LENGTH_LONG).show();
        }catch (Exception e){

            Toast.makeText(context,
                    "SMS failed, please try again later ! ",
                    Toast.LENGTH_LONG).show();
            Log.e("Error", e.getLocalizedMessage());
            e.printStackTrace();

        }
    }

}
