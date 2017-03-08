package com.example.pbkou.smarthouse;


import android.*;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbkou.smarthouse.Database.CurrentLocationService;
import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.Database.LoginActivity;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private TextView mTextView;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    private CurrentLocationService clservice;
    private static int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION=3;
    private HashMap firebase_data;
    private SharedPreferences preferences;
    private String user_name="";
    private String user_id="";
    private String house_num="";
    private HashMap<String,String> users_locations = new HashMap<String,String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ask for coarse location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(
                        android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
                }

                requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                return;
            }
        }


        //Get the preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //get user name
        user_name = preferences.getString("user_name","");
        user_id=preferences.getString("user","");
        house_num=preferences.getString("house_num","");
        if (user_name.equals("")) {
            findViewById(R.id.m_loadingPanel).setVisibility(View.GONE);
        } else {
            loadData();
            TextView hello_tv = (TextView) findViewById(R.id.hello_tv);
            hello_tv.setText("Hello "+user_name);
            hello_tv.setTextSize(32);
            clservice=new CurrentLocationService();
            clservice.startUpdatingCurrentLocation(this);
        }




        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //get intent
        Intent intent = getIntent();
        Integer room_num = intent.getIntExtra("room_num", 0);

        //Test start database
        subscribeToPushService();
        DBHandler handler = new DBHandler(getBaseContext());
        //Set content_main padding
        int actionBarHeight = 0;

        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        LinearLayout r_layout = (LinearLayout) findViewById(R.id.content_main);
        r_layout.setPadding(16, actionBarHeight, 16, 16);

        Button change_activity_btn = (Button) findViewById(R.id.change_activity);
        final Intent change_activity_intent = new Intent(this, LoginActivity.class);
        change_activity_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(change_activity_intent);
            }
        });
        System.out.println("About to enter tasks");
        Button tasks_btn = (Button) findViewById(R.id.btn_tasks);
        tasks_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent tasks_intent = new Intent(v.getContext(), Tasks.class);
                startActivity(tasks_intent);
            }
        });
        mTextView = (TextView) findViewById(R.id.textView_explanation);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

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






    private void subscribeToPushService() {
        FirebaseMessaging.getInstance().subscribeToTopic("news");
    private void loadData(){
        final DatabaseReference mDatabase = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("house_numbers")
                .child(house_num)
                .child("users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebase_data = (HashMap) dataSnapshot.getValue();
                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {
                    String loc = (String)childSnap.child("location").getValue();
                    String name = (String)childSnap.child("name").getValue();
                    users_locations.put(name,loc);
                }
                LinearLayout ll = (LinearLayout) findViewById(R.id.home_ll);
                for (Map.Entry<String, String> entry : users_locations.entrySet())
                {

                    TextView tv = new TextView(getBaseContext());
                    tv.setText(entry.getKey()+"  "+ entry.getValue());
                    tv.setTextSize(16);
                    tv.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (entry.getValue().equals("Absent")){
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_brightness_3_black_24dp, 0, 0, 0);
                    }else{
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_brightness_2_black_24dp, 0, 0, 0);
                    }
                    ll.addView(tv);
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                }


                findViewById(R.id.m_loadingPanel).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.toString());
            }
        });
    }

        Log.d("AndroidBash", "Subscribed");
        Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();

        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        Log.d("AndroidBash", token);
        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
    }
}