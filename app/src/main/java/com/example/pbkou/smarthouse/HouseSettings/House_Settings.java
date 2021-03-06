package com.example.pbkou.smarthouse.HouseSettings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.example.pbkou.smarthouse.R;

public class House_Settings extends AppCompatActivity {


    private String role ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house__settings);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Get preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String admin = preferences.getString("role","");
        if (!admin.isEmpty()) {
            if (admin.equals("admin")) role ="admin";
            else role="user";
        }else{
            role="unidentified";
        }
        System.out.println(role);


        Button add_beacon_btn = (Button) findViewById(R.id.add_beacon);
        //Set this visible if admin
        if(role.equals("admin")) add_beacon_btn.setVisibility(View.VISIBLE);
        final Intent beacon_select_intent = new Intent(this,Add_BeaconActivity.class);
        add_beacon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("meow");
                startActivity(beacon_select_intent);
            }
        });

        Button view_beacons = (Button) findViewById(R.id.beacon_areas);
        final Intent beacon_view_intent = new Intent(this,ViewAllBeacons.class);
        view_beacons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(beacon_view_intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.house_settings:
                Intent intent = new Intent(this,House_Settings.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
