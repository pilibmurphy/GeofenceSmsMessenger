package com.example.geofencing;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import static com.example.geofencing.util.jsonUtils.getStringFromFile;

public class GeofenceDetails {

    private static final String TAG = "Geofence_details";
    @SerializedName("fenceId")
    private String fenceId;
    @SerializedName("name")
    private String name;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("enterMessage")
    private String enterMessage;
    @SerializedName("leaveMessage")
    private String leaveMessage;
    @SerializedName("repeating")
    private boolean repeating;
    @SerializedName("Leaving")
    private boolean leaving;
    @SerializedName("entering")
    private boolean entering;

    public GeofenceDetails(String fenceId) {
        this.fenceId = fenceId;
    }

    //todo
    //  will also need to identify transition types
    //  will need to make phone numbers a list instead
    //  Add the lat/lng and radius so that we can draw a map showing all the currently active fences
    //  somehow filter messages and make sure they don't crash the application


    public String getFenceId() {
        return fenceId;
    }

    public void setFenceId(String fenceId) {
        this.fenceId = fenceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEnterMessage() {
        return enterMessage;
    }

    public String getLeavingMessage() {
        return leaveMessage;
    }

    public void setEnterMessage(String message) {
        this.enterMessage = message;
    }

    public void setLeavingMessage(String message) {
        this.leaveMessage = message;
    }

    public String getLeaveMessage() {
        return leaveMessage;
    }

    public void setLeaveMessage(String leaveMessage) {
        this.leaveMessage = leaveMessage;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public boolean isLeaving() {
        return leaving;
    }

    public void setLeaving(boolean Leaving) {
        this.leaving = leaving;
    }

    public boolean isEntering() {
        return entering;
    }

    public void setEntering(boolean entering) {
        this.entering = entering;
    }

    public void delete(){
    }

    //todo store the latlng and the radius so we can see all of them on the map.
}
