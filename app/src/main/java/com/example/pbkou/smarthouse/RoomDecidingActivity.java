package com.example.pbkou.smarthouse;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.Database.LoginActivity;
import com.example.pbkou.smarthouse.HouseSettings.ViewAllBeacons;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;

public class RoomDecidingActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private SharedPreferences preferences;
    private static final String TAG = "RoomDecidingActivity";
    private Long last_room=0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_deciding);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //get the user id
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        final String user = sharedPref.getString("user","");

        //load data
        loadData();

        //Join button
        Button join_house_btn = (Button) findViewById(R.id.join_house_btn);
        join_house_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(RoomDecidingActivity.this);

                final EditText et = new EditText(RoomDecidingActivity.this);
                et.setText("Room number");
                et.setTextSize(28);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(et);



                // set dialog message
                alertDialogBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Integer room_num ;
                        try {
                            //make sure they gave a number
                            room_num = Integer.parseInt(et.getText().toString());

                            //get database reference
                            mDatabase= FirebaseDatabase.getInstance().getReference();

                            if(!user.isEmpty()) {
                                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                                intent.putExtra("room_num",room_num);

                                mDatabase.child("room").child(room_num.toString()).child("users").child(user).setValue(0);
                                mDatabase.child("Users").child(user).child("room").setValue(room_num);
                                startActivity(intent);
                            }

                            //mDatabase.child(android_id).child("smart_reminding").setValue(true);
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

                et.setTextSize(28);
                final Long room_num = last_room;
                et.setText("Room number : "+room_num);
                // set prompts.xml to alertdialog builder

                alertDialogBuilder.setView(et);

                // set dialog message
                alertDialogBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            mDatabase= FirebaseDatabase.getInstance().getReference();
                            mDatabase.child("Users").child(user).child("room").setValue(room_num);
                            mDatabase.child("room").child(room_num.toString()).child("users").child(user).setValue(0);

                            Intent intent = new Intent(getBaseContext(),MainActivity.class);
                            intent.putExtra("room_num",room_num);
                            startActivity(intent);
                            //mDatabase.child(android_id).child("smart_reminding").setValue(true);
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



    public void updateLastRoomDb(int i){
        //get database reference
        mDatabase= FirebaseDatabase.getInstance().getReference();
        mDatabase.child("last_room").setValue(i);

    }


    private void loadData(){
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("last_room").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                last_room=(Long)dataSnapshot.getValue()+1;
                mDatabase.child("last_room").setValue(last_room+1);
                mDatabase.push();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.toString());
            }
        });
    }
}
