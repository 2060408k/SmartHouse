package com.example.pbkou.smarthouse;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.Database.LoginActivity;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private NfcAdapter mNfcAdapter;
    private TextView mTextView;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String TAG = "NfcDemo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //get intent
        Intent intent = getIntent();
        Integer room_num = intent.getIntExtra("room_num", 0);
        System.out.println(room_num);

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

        Log.d("AndroidBash", "Subscribed");
        Toast.makeText(MainActivity.this, "Subscribed", Toast.LENGTH_SHORT).show();

        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        Log.d("AndroidBash", token);
        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
    }
}