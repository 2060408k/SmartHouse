package com.example.pbkou.smarthouse.HouseSettings;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pbkou.smarthouse.Beacon;
import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ViewAllBeacons extends AppCompatActivity {

    private DatabaseReference mDatabase;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_beacons);


        //Get the preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        LinearLayout main_view = (LinearLayout) findViewById(R.id.beacon_scroll_view);

        DBHandler dbhandler = new DBHandler(ViewAllBeacons.this);
        ArrayList<Beacon> all_beacons = dbhandler.getAllBeacons();
        for (final Beacon beacon : all_beacons){
            final Beacon temp_beacon = beacon;
            TextView tv = new TextView(this);
            tv.setText(beacon.getArea() + "\n"+beacon.getName() + "\n" + beacon.getAddress());
            tv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(32);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ViewAllBeacons.this);

                    final TextView et = new TextView(ViewAllBeacons.this);
                    et.setText(R.string.delete_beacon_dialog_title);
                    et.setTextSize(28);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(et);

                    // set dialog message
                    alertDialogBuilder.setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            //delete beacon from local database
                            DBHandler dbhandler = new DBHandler(ViewAllBeacons.this);
                            dbhandler.deleteBeacon(beacon.getName());

                            //delete beacon from online database
                            String house_num = preferences.getString("house_num","");
                            String admin = preferences.getString("role","");

                            if(!house_num.isEmpty() && admin.equals("admin")) {
                                mDatabase = FirebaseDatabase.getInstance().getReference().child("house_numbers");
                                mDatabase.child(house_num).child("rooms").child(temp_beacon.getArea()).removeValue();
                            }


                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);


                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    // create alert dialog
                    android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    // show it
                    alertDialog.show();
                }
            });
            main_view.addView(tv);
        }


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
