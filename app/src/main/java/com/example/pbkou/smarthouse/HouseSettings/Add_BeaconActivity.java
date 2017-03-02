package com.example.pbkou.smarthouse.HouseSettings;

import android.content.Context;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbkou.smarthouse.Beacon;
import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Add_BeaconActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private String selectedBeacon="";

    private Context context;

    private EditText area;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beacon_);


        //Get the preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //select beacon btn
        Button select_beacon_btn =  (Button) findViewById(R.id.add_beacon_select_beacon);
        final Intent select_beacon_intent = new Intent(this,Beacon_SelectActivity.class);
        select_beacon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(select_beacon_intent);
            }
        });

        //set the BeaconName editext
        final EditText beacon_name_txt = (EditText) findViewById(R.id.add_beacon_beacon_name);
        setArea(beacon_name_txt);


        //add btn
        Button add_btn= (Button) findViewById(R.id.add_beacon_add);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear the beacon text
                clearSelectedBeaconText();

                //create beacon object
                String sel_beacon = getSelectedBeacon();
                String[] sel_beacon_prop = sel_beacon.split("/");
                if(sel_beacon_prop.length<2){
                    Toast.makeText(Add_BeaconActivity.this,"Beacon not selected",Toast.LENGTH_LONG).show();
                    return;
                }
                Beacon beacon = new Beacon(sel_beacon_prop[0],sel_beacon_prop[1],beacon_name_txt.getText().toString());

                //get database and add the beacon
                //// TODO: 21/02/2017  Add checks for beacon already existing
                DBHandler dbhandler = new DBHandler(Add_BeaconActivity.this);
                boolean beacon_already_exists= false;
                boolean area_already_exists= false;
                for (Beacon b :dbhandler.getAllBeacons()){
                    if (b.getName().equals(beacon.getName())) beacon_already_exists=true;
                    if (b.getArea().equals(beacon.getArea())) area_already_exists=true;
                }

                if (!beacon_already_exists || !area_already_exists){
                    dbhandler.addBeacon(beacon);
                    addBeaconToFirebase(beacon);
                    onBackPressed();
                }else{
                    if(beacon_already_exists && area_already_exists)
                        Toast.makeText(Add_BeaconActivity.this,"Beacon and Area already exists",Toast.LENGTH_LONG).show();
                    if(beacon_already_exists )
                        Toast.makeText(Add_BeaconActivity.this,"Beacon already exists",Toast.LENGTH_LONG).show();
                    if(area_already_exists)
                        Toast.makeText(Add_BeaconActivity.this,"Area already exists",Toast.LENGTH_LONG).show();

                }


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

    @Override
    public void onResume(){
        //set beacon selected tv
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println("resuming");
        TextView beacon_selected_tv = (TextView) findViewById(R.id.add_beacon_selected_beacon_name);
        String beacon_selected = sharedPref.getString("selected_beacon","");
        if(!beacon_selected.isEmpty()) {
            beacon_selected_tv.setText(beacon_selected);
            setSelectedBeacon(beacon_selected);
        }

        super.onResume();

    }
    @Override
    public void onBackPressed() {
        clearSelectedBeaconText();
        clearSelectedBeaconSelectedText();
        super.onBackPressed();

    }

    protected void clearSelectedBeaconText() {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("selected_house_beacon_name","");
        editor.commit();
    }
    protected void clearSelectedBeaconSelectedText() {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("selected_beacon","");
        editor.commit();
    }

    public void onDestroy() {
        clearSelectedBeaconText();
        clearSelectedBeaconSelectedText();
        super.onDestroy();

    }

    public void addBeaconToFirebase(Beacon beacon){
        String house_num = preferences.getString("house_num","");
        String admin = preferences.getString("role","");
        System.out.println(house_num + " " + admin);
        if(!house_num.isEmpty() && admin.equals("admin")){
            mDatabase= FirebaseDatabase.getInstance().getReference().child("house_numbers").child(house_num).child("rooms").child(beacon.getArea());
            mDatabase.child("beacon_id").setValue(beacon.getId());
            mDatabase.child("beacon_address").setValue(beacon.getAddress());
            mDatabase.child("beacon_name").setValue(beacon.getName());
        }
    }

    //Getters-Setters
    public String getSelectedBeacon() {
        return selectedBeacon;
    }

    public void setSelectedBeacon(String selectedBeacon) {
        this.selectedBeacon = selectedBeacon;
    }

    public EditText getArea() {
        return area;
    }

    public void setArea(EditText area) {
        this.area = area;
    }
}
