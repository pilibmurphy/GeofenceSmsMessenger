package com.example.geofencing;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.example.geofencing.util.jsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GeofenceActivity extends AppCompatActivity {
    private final String TAG = "newGeofence";
    private EditText et_name;
    private EditText et_number;
    private EditText et_enterMsg;
    private EditText et_leaveMsg;
    private Switch s_leaving;
    private Switch s_entering;
    private Switch s_repeating;

    private Button btnSave;
    private String fenceID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_geofence);
        screenElements();
        fenceID = UUID.randomUUID().toString();
    }

    private void save() throws Exception {

        //User inputs details for the geofence and they are then saved to file.
        //geofence on file and real geofences are connected by the fence id created in maps activity
        //todo - needs to be tested and error management

        GeofenceDetails newGeofence = new GeofenceDetails(fenceID);
        newGeofence.setName(et_name.getText().toString());
        newGeofence.setPhoneNumber(et_number.getText().toString());
        newGeofence.setEnterMessage(et_enterMsg.getText().toString());
        newGeofence.setLeaving(s_leaving.isChecked());
        newGeofence.setLeaving(s_entering.isChecked());
        newGeofence.setLeavingMessage(et_leaveMsg.getText().toString());
        newGeofence.setEntering(s_entering.isChecked());
        newGeofence.setRepeating(s_repeating.isChecked());

        // todo get the details form the screen

        File fileJson = new File(getApplicationContext().getExternalFilesDir("/app"), "app.json");
        //fileJson.delete();
        Gson gson = new Gson();

        if (!fileJson.exists()) {
            Log.e(TAG, "file does not exist, creating new");
            List<GeofenceDetails> geofences = new ArrayList<>();
            geofences.add(newGeofence);
            String jsonString = gson.toJson(geofences);
            jsonUtils.writeJsonFile(fileJson, jsonString);
            jsonString = jsonUtils.getStringFromFile(fileJson.toString());
            Log.e(TAG, "first input into file: " +jsonString);

        } else {
            Log.e(TAG, "file exist, appending");
            String jsonString = jsonUtils.getStringFromFile(fileJson.toString());
            Type geofenceDetails = new TypeToken<ArrayList<GeofenceDetails>>() {}.getType();
            ArrayList<GeofenceDetails> geofences = gson.fromJson(jsonString, geofenceDetails);

            geofences.add(newGeofence);
            jsonString = gson.toJson(geofences);
            jsonUtils.writeJsonFile(fileJson, jsonString);
            jsonString = jsonUtils.getStringFromFile(fileJson.toString());
            Log.e(TAG, "new addition to array: " +jsonString);
        }
    }

    private void screenElements(){
        et_name = (EditText)findViewById(R.id.editTextTextPersonName);
        et_number = (EditText)findViewById(R.id.editTextPhoneNumber);
        et_enterMsg = (EditText)findViewById(R.id.editTextEnterGeo);
        btnSave = (Button)findViewById(R.id.buttonNext);
        et_leaveMsg = findViewById(R.id.editTextTextLeaveGeo);
        s_leaving = findViewById(R.id.switchLeavingGeo);
        s_entering = findViewById(R.id.switchEnteringGeo);
        s_repeating = findViewById(R.id.switchRepeating);

        findViewById(R.id.buttonNext).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.e(TAG,"attempting Save");
                    save();
                    Log.e(TAG,"save complete");
                    Intent myIntent = new Intent(GeofenceActivity.this, MapsActivity.class);
                    myIntent.putExtra("fenceID", fenceID);
                    GeofenceActivity.this.startActivity(myIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
