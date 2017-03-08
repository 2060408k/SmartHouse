package com.example.pbkou.smarthouse;

import android.app.DownloadManager;
import android.app.LocalActivityManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.Database.LoginActivity;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Alexiah on 23/02/2017.
 */

public class Tasks extends AppCompatActivity {
    private SharedPreferences preferences;
    private DatabaseReference mDatabase;
    private HashMap firebase_data;
    private HashMap tasksHash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context mContext= this.getBaseContext();
        setContentView(R.layout.activity_show_tasks);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //set the floating button to add a new task
        final FloatingActionButton add_task = (FloatingActionButton) findViewById(R.id.btn_add_task);
        final Intent intent = new Intent(this,AddTask.class);
        add_task.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        DBHandler dbhandler = new DBHandler(getBaseContext());
        ListView mListView = (ListView)findViewById(R.id.tasks_scroll_view);
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user = preferences.getString("user","");
        String houseNum = preferences.getString("house_num","");
        loadData(houseNum,user);

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


    private void loadData(String house_num, String user) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("house_numbers").child(house_num).child("users").child(user).child("tasks_from");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tasksHash = (HashMap) dataSnapshot.getValue();
                Iterator it = tasksHash.entrySet().iterator();
                ListView mListView = (ListView) findViewById(R.id.tasks_scroll_view);
                final ArrayList<String> content = new ArrayList<String>();

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    Iterator it2 = ((HashMap) pair.getValue()).entrySet().iterator();
                    String body="";
                    String user_from = "";
                    while (it2.hasNext()) {
                        Map.Entry pair2 = (Map.Entry) it2.next();
                        if (pair2.getKey().toString().equals("body")) {
                            body = pair2.getValue().toString();
                        }
                        if (pair2.getKey().toString().equals("user_from")) {
                            user_from = pair2.getValue().toString();
                        }
                        if (pair2.getKey().toString().equals("date")) {
                            String date = pair2.getValue().toString();
                        }
                    }
                    content.add("Task: " + body + "\n" + "From: " + user_from);
                    it.remove(); // avoids a ConcurrentModificationException
                }

                String[] listItems = new String[content.size()];
                for (int i=0; i<content.size(); i++){
                    String task = content.get(i);
                    listItems[i] = task;
                }
                ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1,listItems);
                mListView.setAdapter(adapter);
                //setContentView(mListView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}



