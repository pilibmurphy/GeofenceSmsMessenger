package com.example.geofencing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.geofencing.util.jsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener  {
    MyRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button_create_geofence).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, GeofenceActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });

    }

    private void recView(){

        //todo extract all the json objects and convert them to java objects for the recycler view
        // if the string returned is NULL, no view and a text of "No GeoFences setup"

        Gson gson = new Gson();
        File fileJson = new File(getApplicationContext().getExternalFilesDir("/app"), "app.json");
        String jsonString = null;
        try {
            jsonString = jsonUtils.getStringFromFile(fileJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Type geofenceDetails = new TypeToken<ArrayList<GeofenceDetails>>() {}.getType();
        ArrayList<GeofenceDetails> geofences = gson.fromJson(jsonString, geofenceDetails);

        RecyclerView recyclerView = findViewById(R.id.rc_mainList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, geofences);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
