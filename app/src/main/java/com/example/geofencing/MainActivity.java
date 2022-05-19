package com.example.geofencing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.geofencing.util.jsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener  {
    private static final String TAG = "MainActivity";
    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screenElements();

    }

    private void recView(){

        //todo extract all the json objects and convert them to java objects for the recycler view
        // if NULL, set a textview: "No GeoFences setup"

        Gson gson = new Gson();
        File fileJson = new File(getApplicationContext().getExternalFilesDir("/app"), "app.json");
        String jsonString = null;
        try {
            jsonString = jsonUtils.getStringFromFile(fileJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // add the rec view here
        /*Type geofenceDetails = new TypeToken<ArrayList<GeofenceDetails>>() {}.getType();
        ArrayList<GeofenceDetails> geofences = gson.fromJson(jsonString, geofenceDetails);

        RecyclerView recyclerView = findViewById(R.id.rc_mainList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, geofences);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);*/

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }

    private void screenElements() {
        findViewById(R.id.button_create_geofence).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, GeofenceActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
        findViewById(R.id.delete_json).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteJson();
            }
        });

        try {
            loadJsonList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteJson(){
        File fileJson = new File(getApplicationContext().getExternalFilesDir("/app"), "app.json");
        if (fileJson.exists()){
            jsonUtils.writeJsonFile(fileJson, "{}");
        }
        // in end match by uuid and then delete the BC also.
    }


    private void loadJsonList() throws Exception {
        TextView gsonList =  findViewById(R.id.GsonList);
        File fileJson = new File(getApplicationContext().getExternalFilesDir("/app"), "app.json");
        //fileJson.delete();
        Gson gson = new Gson();

        //todo I have used this twice -- make utils folder to manage this.
        if (!fileJson.exists()) {
            Log.e(TAG, "file does not exist");
            gsonList.setText("No Geofences Present");
        } else {
            Log.e(TAG, "getting json");
            String jsonString = jsonUtils.getStringFromFile(fileJson.toString());
            Type geofenceDetails = new TypeToken<ArrayList<GeofenceDetails>>() {}.getType();
            ArrayList<GeofenceDetails> geofences = gson.fromJson(jsonString, geofenceDetails);
            StringBuilder list = new StringBuilder();
            for(GeofenceDetails geof :  geofences){
                list.append(geof.getName());
                list.append("\n");
                list.append(geof.getFenceId());
                list.append("\n");
                list.append(geof.getPhoneNumber());
                list.append("\n");
                list.append(geof.getEnterMessage());
                list.append("\n");
                list.append("--------------------------------------------------------------\n");
            }
            gsonList.setText(list);
        }
    }
}
/*
    Might be able to publish the application under this use case:

    In-vehicle hands-free use and projected display
    Apps directly related to core functionalities of driving/mobility (e.g., navigation),
    especially in situations where a user’s physical interactions with a device(s) are limited
 */
