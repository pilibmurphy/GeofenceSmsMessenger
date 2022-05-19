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

import com.example.geofencing.util.jsonUtils;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BroadcastReceiver";

    //This might help with viewing all the active ones.
    //List<ResolveInfo> receivers = getPackageManager().queryBroadcastReceivers(new Intent("android.provider.Telephony.SMS_RECEIVED"), 0);
    //https://stackoverflow.com/questions/25262875/get-all-registered-broadcastreceivers-for-received-sms

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "bcReceiver is working");

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...");
            return;
        }
        String testid = "";
        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence: geofenceList) {
            Log.e(TAG, "onReceive: " + geofence.getRequestId());
            Log.e(TAG, "fenceid: " + geofence.getRequestId());
            testid = geofence.getRequestId();
        }

        // Can i use this for anything cool?
        //Location location = geofencingEvent.getTriggeringLocation();

        File fileJson = new File(context.getExternalFilesDir("/app"), "app.json");
        Gson gson = new Gson();

        if (fileJson.exists()) {
            String jsonString = null;
            try {
                jsonString = jsonUtils.getStringFromFile(fileJson.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Type geofenceDetails = new TypeToken<ArrayList<GeofenceDetails>>() {}.getType();
            ArrayList<GeofenceDetails> geofences = gson.fromJson(jsonString, geofenceDetails);
            boolean removed = false;
            for(GeofenceDetails geof : geofences) {
                if(geof.getFenceId().equals(testid)) {

                    SmsManager smsManager = SmsManager.getDefault();
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        sendText(context, geof);
                    }

                    notificationHelper.sendHighPriorityNotification("Attempting text: " + geof.getName(), "", MapsActivity.class);
                    if(!geof.isRepeating()){
                        geofences.remove(geof);
                        //context.unregisterReceiver(mReceiver);
                        removed = true;
                    }
                }
            }

            //  rewrite the file without the deleted geofence.
            if (removed){
                jsonString = gson.toJson(geofences);
                jsonUtils.writeJsonFile(fileJson, jsonString);
            }

        } else {
            Log.e(TAG, "This is awkward... ");
        }


        //todo reintroduce the different transitionTypes and not just the entering
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


        //notificationHelper.sendHighPriorityNotification("Attempting to send Text Message", "", MapsActivity.class);
    }

    //todo - use notification rather than toasts as the application might not be open.
    //      infact I think they might cause an error?
    private void sendText(Context context, GeofenceDetails geof){

        String phoneNo = geof.getPhoneNumber();
        String messageText = geof.getEnterMessage();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, messageText, null, null);
            //Toast.makeText(context, "SMS Sent Successfully!", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            //Toast.makeText(context, "SMS failed, please try again later ! ", Toast.LENGTH_LONG).show();
            Log.e("Error", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}

    /*
        It is better to let single BroadcastReceiver handle single event (SOLID principle).
        However, you can register several receivers, each receiving different event.
        There is another problem in your code - BroadcastReceiver is running on main thread and Android suppose that code in BroadcastReceiver will finish very fast.
        You should not do any long running actions (writing to database) in it. Instead,
        get received data and start IntentService for processing that data by passing received values via Intent
    */
