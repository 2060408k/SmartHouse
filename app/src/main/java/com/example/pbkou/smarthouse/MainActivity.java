package com.example.pbkou.smarthouse;


import android.*;
import android.app.Activity;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class MainActivity extends AppCompatActivity  {
    private DatabaseReference mDatabase;
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
    private BluetoothAdapter mBluetoothAdapter ;
    private final static int REQUEST_ENABLE_BT=1;
    private LocationManager lm;
    private Location location;
    private NotificationService notificationService;
    private TextView txtView;
    //private NotificationReceiver nReceiver;


    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            SharedPreferences.Editor editor =  preferences.edit();
            String lat =location.getLatitude()+"";
            String lon = location.getLongitude()+"";
            System.out.println(lat + lon);
            editor.putString("latitude_dbl",lat);
            editor.putString("longitude_dbl",lon);
            editor.commit();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


        //Get the preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //location
        lm = (LocationManager)getSystemService(getBaseContext().LOCATION_SERVICE);

        if (checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED ){
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }


        if (location!=null){
            SharedPreferences.Editor editor =  preferences.edit();
            String lat =location.getLatitude()+"";
            String lon = location.getLongitude()+"";
            System.out.println(lat + lon);
            editor.putString("latitude_dbl",lat);
            editor.putString("longitude_dbl",lon);
            editor.commit();
        }


        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);

        //ask for bluetooth
        if(!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }



        //get user name
        user_name = preferences.getString("user_name","");
        user_id=preferences.getString("user","");
        house_num=preferences.getString("house_num","");
        System.out.println(user_name+ " " +user_id + " " +house_num);
        if (user_name.equals("")) {
            findViewById(R.id.m_loadingPanel).setVisibility(View.GONE);
        } else {
            loadData();
            TextView hello_tv = (TextView) findViewById(R.id.hello_tv);
            hello_tv.setText("Hello "+user_name.split(" ")[0]);
            hello_tv.setTextSize(38);
            hello_tv.setGravity(Gravity.CENTER_HORIZONTAL);

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


        ImageButton send_btn = (ImageButton) findViewById(R.id.add_not_btn);
        send_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent send_not_intent = new Intent(v.getContext(), SendNotification.class);
                startActivity(send_not_intent);
            }
        });

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        addFirebaseUpdateListener();
//        txtView = (TextView) findViewById(R.id.textView);
//        nReceiver = new NotificationReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
//        registerReceiver(nReceiver,filter);

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
            case R.id.activity_notifications:
                startActivity(new Intent(this,notifications.class));
                break;
            case R.id.add_conv_add:
                startActivity(new Intent(this,Conversations.class));
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
        Log.d("AndroidBash", "Subscribed");
        //Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();
        String token = FirebaseInstanceId.getInstance().getToken();
        //Get the preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String houseNum = preferences.getString("house_num", "");
        //get database reference
        mDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child("house_numbers")
                .child(houseNum.toString());
        final String user = preferences.getString("user", "");
        mDatabase.child("users").child(user).child("token").setValue(token);

        // Log and toast
        Log.d("AndroidBash", token);
        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
    }

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
                    String text = entry.getKey()+"";
                    if(entry == null || entry.getValue()==null || entry.getKey()==null ) continue;
                    if (entry!=null && entry.getValue()!=null && !entry.getValue().contains("_")) text = entry.getKey()+"  "+ entry.getValue();
                    tv.setText(text);
                    tv.setTextSize(24);
                    tv.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (entry!=null && entry.getValue()!=null && (entry.getValue().equals("Absent") ||  entry.getValue().contains("_"))){
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_brightness_3_black_24dp, 0, 0, 0);
                    }else{
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_brightness_2_black_24dp, 0, 0, 0);
                    }
                    ll.addView(tv);
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                    if(entry!=null && entry.getValue()!=null && entry.getValue().contains("_")){
                        final String loc =entry.getValue();
                        tv.setTextColor(Color.BLUE);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext(),MapActivity.class);
                                intent.putExtra("USER_LOCATION",loc);
                                startActivity(intent);
                            }
                        });
                    }

                }


                findViewById(R.id.m_loadingPanel).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("Error", databaseError.toString());
            }
        });


    }

    public void addFirebaseUpdateListener(){
        final DatabaseReference mDatabase = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("house_numbers")
                .child(house_num)
                .child("users");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                findViewById(R.id.m_loadingPanel).setVisibility(View.VISIBLE);

                LinearLayout home_ll = (LinearLayout) findViewById(R.id.home_ll);
                home_ll.removeAllViews();

                firebase_data = (HashMap) dataSnapshot.getValue();

                for (DataSnapshot childSnap : dataSnapshot.getChildren()) {
                    String loc = (String)childSnap.child("location").getValue();
                    String name = (String)childSnap.child("name").getValue();
                    users_locations.put(name,loc);
                }


                for (Map.Entry<String, String> entry : users_locations.entrySet())
                {
                    TextView tv = new TextView(getBaseContext());
                    String text = entry.getKey()+"";
                    if(entry == null || entry.getValue()==null || entry.getKey()==null ) continue;
                    if (entry!=null && entry.getValue()!=null && !entry.getValue().contains("_")) text = entry.getKey()+"  "+ entry.getValue();
                    tv.setText(text);
                    tv.setTextSize(24);
                    tv.setLayoutParams(new ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (entry!=null && entry.getValue()!=null && (entry.getValue().equals("Absent") ||  entry.getValue().contains("_"))){
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_brightness_3_black_24dp, 0, 0, 0);
                    }else{
                        tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_brightness_2_black_24dp, 0, 0, 0);
                    }
                    home_ll.addView(tv);
                    System.out.println(entry.getKey() + "/" + entry.getValue());
                    if(entry!=null && entry.getValue()!=null && entry.getValue().contains("_")){
                        final String loc =entry.getValue();
                        tv.setTextColor(Color.BLUE);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext(),MapActivity.class);
                                intent.putExtra("USER_LOCATION",loc);
                                startActivity(intent);
                            }
                        });
                    }

                }

                findViewById(R.id.m_loadingPanel).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



//    class NotificationReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String temp = intent.getStringExtra("notification_event") + "n";
//            System.out.println(temp + " MEOW");
//        }
//    }


}