package com.example.pbkou.smarthouse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

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
 * Created by Alexiah on 09/03/2017.
 */

public class SendNotification extends AppCompatActivity {
    private HashMap tokens;
    private SharedPreferences preferences;
    private DatabaseReference mDatabase;
    private String LEGACY_SERVER_KEY = "AIzaSyCNaVj28sqnozHtwgziTtuCKQIcBIJ49Jk";
    private int index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notifications);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //Get current user name
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String houseNum = preferences.getString("house_num","");
        loadData(houseNum);

    }

    private void loadData(String house_num) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("house_numbers").child(house_num).child("users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //set the body task editext
                final EditText not_body_txt = (EditText) findViewById(R.id.not_body);
                not_body_txt.requestFocus();
                final Button send_not = (Button)findViewById(R.id.btn_send_not);

                tokens = (HashMap) dataSnapshot.getValue();
                Iterator it = tokens.entrySet().iterator();
                ListView mListView = (ListView) findViewById(R.id.users_scroll_view);
                final ArrayList<String> userTokens = new ArrayList<String>();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    String userid = pair.getKey().toString();
                    Iterator it2 = ((HashMap) pair.getValue()).entrySet().iterator();
                    while (it2.hasNext()) {
                        Map.Entry pair2 = (Map.Entry) it2.next();
                        if (pair2.getKey().toString().equals("token")) {
                            userTokens.add(pair2.getValue().toString());
                        }
                        it2.remove();
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }
                send_not.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        for (int i=0; i<userTokens.size(); i++){
                            sendNotification(userTokens.get(i),not_body_txt.getText().toString());
                        }

                        Intent intent = new Intent(getBaseContext(),MainActivity.class);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private void sendNotification(final String token, final String body) {
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        OkHttpClient client = new OkHttpClient();
                        JSONObject json=new JSONObject();
                        JSONObject dataJson=new JSONObject();
                        dataJson.put("body",body);
                        dataJson.put("title","SmartHouse");
                        json.put("notification",dataJson);
                        json.put("to",token);
                        System.out.println("**Token: "+token);

                        RequestBody body = RequestBody.create(JSON, json.toString());
                        Request request = new Request.Builder()
                                .header("Authorization","key="+ LEGACY_SERVER_KEY)
                                .url("https://fcm.googleapis.com/fcm/send")
                                .post(body)
                                .build();
                        Response response = client.newCall(request).execute();
                        String finalResponse = response.body().string();
                    }catch (Exception e){
                    }
                    return null;
                }
            }.execute();

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
}