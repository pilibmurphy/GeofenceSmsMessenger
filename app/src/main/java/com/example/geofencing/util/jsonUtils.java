package com.example.geofencing.util;

import android.util.Log;

import com.example.geofencing.GeofenceDetails;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class jsonUtils {
    public static final String TAG = "JSON_UTILS";


    //todo - make another method to convert the GeofenceDetails object to json, very possibly won't as I have this working and am lazy :)

    public static void createNewJson(File fileJson, GeofenceDetails geoFenceDetails) throws Exception {
        fileJson.createNewFile();

        JSONObject jsonObj = new JSONObject();
        JSONObject item = new JSONObject();

        //item.put("geofenceID", geoFenceDetails.getFenceId());
        item.put("name", geoFenceDetails.getName());
        item.put("number", geoFenceDetails.getPhoneNumber());
        item.put("enterMsg", geoFenceDetails.getEnterMessage());
        item.put("leaveMsg", geoFenceDetails.getLeaveMessage());
        item.put("repeating", geoFenceDetails.isRepeating());
        item.put("sendLeavingMessage", geoFenceDetails.isLeaving());
        jsonObj.put(geoFenceDetails.getFenceId(), item);

        writeJsonFile(fileJson, jsonObj.toString());

        Log.e(TAG,"wrote file");
        String strFileJson = getStringFromFile(fileJson.toString());
        Log.e(TAG, strFileJson);
    }

    public static void appendExistingJson(File fileJson, GeofenceDetails geoFenceDetails) throws JSONException {
        String strFileJson = null;
        try {
            Log.e(TAG,"getting string from file");
            strFileJson = getStringFromFile(fileJson.toString());
            Log.e(TAG,"got string from file");
            Log.e("TAG json should be:", strFileJson);
        } catch (Exception e) {
            Log.e(TAG, "one" + e.getLocalizedMessage());
        }

        JSONObject jsonObj = null;
        try {
            Log.e(TAG,"creating the new json object");
            jsonObj = new JSONObject(strFileJson);
            Log.e(TAG,"created successfully");
        } catch (JSONException e) {
            //something bad has went wrong....
        }
        Log.e("jsonobj loaded:", jsonObj.toString());

        //JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();

        item.put("name", geoFenceDetails.getName());
        item.put("number", geoFenceDetails.getPhoneNumber());
        item.put("enterMsg", geoFenceDetails.getEnterMessage());
        item.put("leaveMsg", geoFenceDetails.getLeaveMessage());
        item.put("repeating", geoFenceDetails.isRepeating());
        item.put("sendLeavingMessage", geoFenceDetails.isLeaving());
        jsonObj.put(geoFenceDetails.getFenceId(), item);


        writeJsonFile(fileJson, jsonObj.toString());

        //String name = jsonObj.get("fence_02").toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        fin.close();
        return ret;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public static void writeJsonFile(File file, String json) {
        BufferedWriter bufferedWriter = null;
        try {
            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(json);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static GeofenceDetails load(File jsonFile, String ID){
        //todo load the string json --> make that into a json object --> compare the id --> set all the details.
        String strFileJson = null;
        try {
            Log.e(TAG,"getting string from file");
            strFileJson = getStringFromFile(jsonFile.toString());
            Log.e(TAG,"got string from file");
            Log.e("TAG json should be:", strFileJson);
        } catch (Exception e) {
            Log.e(TAG, "one" + e.getLocalizedMessage());
        }

        Gson gson = new Gson();
        GeofenceDetails geofenceDetails[] = gson.fromJson(strFileJson, GeofenceDetails[].class);
        //Log.e(TAG,  geofenceDetails[0].getFenceId());

        return geofenceDetails[0];
    }

}

/*
JSONObject jsonObj = new JSONObject();

        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();

        item.put("name", geoFenceDetails.getName());
        item.put("number", geoFenceDetails.getPhoneNumber());
        item.put("enterMsg", geoFenceDetails.getEnterMessage());
        item.put("leaveMsg", geoFenceDetails.getLeaveMessage());
        item.put("repeating", geoFenceDetails.isRepeating());
        item.put("sendLeavingMessage", geoFenceDetails.isLeaving());
        array.put(item);
 */
