package com.example.pbkou.smarthouse.HouseSettings;

import android.content.DialogInterface;
import android.content.Intent;
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

import java.util.ArrayList;

public class ViewAllBeacons extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_beacons);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        LinearLayout main_view = (LinearLayout) findViewById(R.id.beacon_scroll_view);

        DBHandler dbhandler = new DBHandler(ViewAllBeacons.this);
        ArrayList<Beacon> all_beacons = dbhandler.getAllBeacons();
        for (Beacon beacon : all_beacons){
            final Beacon temp_beacon = beacon;
            System.out.println(temp_beacon.getId()+"temp");
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

                            DBHandler dbhandler = new DBHandler(ViewAllBeacons.this);
                            System.out.println(dbhandler.deleteBeacon(temp_beacon.getName()));
                            System.out.println(temp_beacon.getId());

                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                            //dialog.dismiss();

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
