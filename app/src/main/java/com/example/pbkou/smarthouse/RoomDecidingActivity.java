package com.example.pbkou.smarthouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.Database.LoginActivity;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.example.pbkou.smarthouse.HouseSettings.ViewAllBeacons;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RoomDecidingActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private Long last_house_num=0L;
    private SharedPreferences preferences;
    private HashMap firebase_data;
    private int creat_house_tv_id = View.generateViewId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_deciding);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Get the preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //get the user id
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String user = sharedPref.getString("user","");
        final String user_name = sharedPref.getString("user_name","");

        //load data
        loadData();

        //Join button
        Button join_house_btn = (Button) findViewById(R.id.join_house_btn);
        join_house_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RoomDecidingActivity.this);

                final EditText et = new EditText(RoomDecidingActivity.this);
                et.setText(R.string.room_number);
                et.setTextSize(28);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(et);



                // set dialog message
                alertDialogBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Integer house_num ;
                        try {
                            //make sure they gave a number
                            house_num = Integer.parseInt(et.getText().toString());

                            //get database reference
                            mDatabase= FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child("house_numbers")
                                    .child(house_num.toString());

                            if(!user.isEmpty()) {

                                //set users data
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("house_num",house_num.toString());
                                editor.putString("role","user");//must change later
                                setUserPrivilages(house_num.toString());
                                editor.apply();

                                //update database
                                mDatabase.child("users").child(user).child("name").setValue(user_name);
                                mDatabase.child("users").child(user).child("location").setValue("Absent");
                                DBHandler dbhandler = new DBHandler(RoomDecidingActivity.this);
                                dbhandler.resetBeaconTable();

                                //get the beacons from database
                                loadBeaconsFromFirebase(house_num.toString());

                                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                                startActivity(intent);
                            }


                        } catch (NumberFormatException ignored) {
                            Toast.makeText(getBaseContext(),"You have not provided a number",Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
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

        //Create Button
        Button create_house_button =  (Button) findViewById(R.id.create_house_btn);
        create_house_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RoomDecidingActivity.this);

                final TextView et = new TextView(RoomDecidingActivity.this);
                et.setId(creat_house_tv_id);
                et.setTextSize(28);
                final Long last_house_number = last_house_num;
                et.setText(getBaseContext().getResources().getString(R.string.room_number)+" "+last_house_number.toString());
                // set prompts.xml to alertdialog builder

                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            mDatabase= FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("house_numbers").child(last_house_number.toString()).child("admin_id").setValue(user);
                            mDatabase.child("house_numbers").child(last_house_number.toString()).child("users").child(user).child("name").setValue(user_name);
                            mDatabase.child("house_numbers").child(last_house_number.toString()).child("users").child(user).child("location").setValue("Absent");
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("house_num",last_house_number.toString());
                            editor.putString("role","admin");//must change later
                            editor.apply();

                            Intent intent = new Intent(getBaseContext(),MainActivity.class);
                            startActivity(intent);
                        } catch (NumberFormatException ignored) {
                            Toast.makeText(getBaseContext(),"You have provided a number",Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        }
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
    }




    private void loadData(){
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebase_data = (HashMap) dataSnapshot.getValue();
                if (!firebase_data.containsKey("last_house")){
                    mDatabase.child("last_house").setValue(1);
                }
                last_house_num = (long)dataSnapshot.child("last_house").getValue();
                //TextView tv = (TextView) findViewById(creat_house_tv_id);
                //tv.setText("Your House number :\n "+last_house_num);
                mDatabase.child("last_house").setValue(last_house_num+1);

                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                //last_house_num= (long)firebase_data.get("last_house");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.toString());
            }
        });
    }

    private void loadBeaconsFromFirebase(String house_num){
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("house_numbers").child(house_num).child("rooms");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<Object,Object> map = (HashMap) dataSnapshot.getValue();
                DBHandler handler = new DBHandler(getBaseContext());
                if(map==null || map.size()==0) return;
                Iterator it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    //System.out.println(pair.getKey() + " = " + pair.getValue());

                    Beacon beacon = new Beacon();

                    //Get key = area
                    beacon.setArea(pair.getKey().toString());

                    HashMap<String,String> values = (HashMap)map.get(pair.getKey().toString());
                    beacon.setAddress(values.get("beacon_address"));
                    beacon.setId(values.get("beacon_id"));
                    beacon.setName(values.get("beacon_name"));

                    handler.addBeacon(beacon);

                    it.remove(); // avoids a ConcurrentModificationException
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.toString());
            }
        });
    }

    public void setUserPrivilages(String house_num){


        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("house_numbers").child(house_num).child("admin_id");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //get from preference the current user id
                String current_user_id = preferences.getString("user","");

                //get the admin id of the house
                String admin_id= (String) dataSnapshot.getValue();

                //if it's this user then make him admin
                if (current_user_id.equals(admin_id)){

                    //get the editor
                    SharedPreferences.Editor editor=preferences.edit();

                    //set in shared preferences the role as admin
                    editor.putString("role","admin");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                startActivity(new Intent(this,House_Settings.class));
                break;
            case R.id.view_conversations:
                startActivity(new Intent(this,Conversations.class));
                break;
            case R.id.activity_notifications:
                startActivity(new Intent(this,notifications.class));
                break;
            case R.id.add_conv_add:
                startActivity(new Intent(this,AddConversation.class));
                break;
            case R.id.activitys:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.activity_show_tasks:
                startActivity(new Intent(this,Tasks.class));
                break;
            case R.id.activity_room_deciding:
                startActivity(new Intent(this,RoomDecidingActivity.class));
                break;
            case R.id.change_activity:
                startActivity(new Intent(this,LoginActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
