package com.example.pbkou.smarthouse;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.Database.LoginActivity;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Alexiah on 03/03/2017.
 */

public class AddTask  extends AppCompatActivity implements View.OnClickListener {
    private HashMap usersHash;
    private SharedPreferences preferences;
    private DatabaseReference mDatabase;
    final ArrayList<String> selectedUsers = new ArrayList<String>();
    private TextView dateView;
    private Calendar calendar;
    private DatePickerDialog datePicker;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    HashMap userID_userName = new HashMap();
    HashMap userID_token = new HashMap();
    private String userid;
    private String LEGACY_SERVER_KEY = "AIzaSyCNaVj28sqnozHtwgziTtuCKQIcBIJ49Jk";
    private String sampleToken ="d51VkyjeN08:APA91bE3Vnhz9XrGcq5HcV-QZ8h6eRLwPNZ6-AGYL1eXUMhJJel2kRcq1iK2UXrQ9LpzMHd0H-3fYkIjgBWUt1OZ35Ea7LK1TZG0fbGMV2TFokqAoJ9KBkZ8NFoRfkIdx46lbhV4zT73";
    // Method to send Notifications from server to client end.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        //Get the preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String houseNum = preferences.getString("house_num", "");
        //get database reference
        mDatabase = FirebaseDatabase.getInstance()
                .getReference()
                .child("house_numbers")
                .child(houseNum.toString());
        final String user = preferences.getString("user", "");
        final String userName = preferences.getString("user_name", "");

        System.out.println(houseNum);
        loadData(houseNum);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //set the body task editext
        final EditText task_body_txt = (EditText) findViewById(R.id.add_task_body);
        task_body_txt.requestFocus();

        //set date
        dateView = (TextView) findViewById(R.id.textView3);
        dateView.setOnClickListener(this);

        calendar = Calendar.getInstance();
        datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dateView.setText(dateFormatter.format(newDate.getTime()));
            }

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        //add btn
        Button add_btn = (Button) findViewById(R.id.add_task_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear the conv text
                clearSelectedConvText();
                DBHandler dbhandler = new DBHandler(getBaseContext());
                Task addTask = new Task(selectedUsers.get(0), dateView.getText().toString(), task_body_txt.getText().toString());

                boolean multipleUsers = false;
                if (selectedUsers.size() > 2) {
                    multipleUsers = true;
                }
                if (!multipleUsers) {
                    dbhandler.createTask(addTask);
                    //Add into user's tasks_to

                    mDatabase.child("users").child(user).child("tasks_to").child(addTask.getTaskId()).child("user_to").setValue(addTask.getUser());
                    mDatabase.child("users").child(user).child("tasks_to").child(addTask.getTaskId()).child("body").setValue(addTask.getBody());
                    mDatabase.child("users").child(user).child("tasks_to").child(addTask.getTaskId()).child("date").setValue(addTask.getDate());
                    //Add into user task_from
                    Iterator it = userID_userName.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        if (pair.getValue().toString().equals(selectedUsers.get(0))) {
                            userid = pair.getKey().toString();
                        }
                        it.remove(); // avoids a ConcurrentModificationException
                    }
                    mDatabase.child("users").child(userid).child("tasks_from").child(addTask.getTaskId()).child("user_from").setValue(userName);
                    mDatabase.child("users").child(userid).child("tasks_from").child(addTask.getTaskId()).child("body").setValue(addTask.getBody());
                    mDatabase.child("users").child(userid).child("tasks_from").child(addTask.getTaskId()).child("date").setValue(addTask.getDate());

                    //Get user token to send notification
                    String token="";
                    Iterator iter = userID_token.entrySet().iterator();
                    while (iter.hasNext()) {
                        Map.Entry pair = (Map.Entry) iter.next();
                        if (pair.getKey().toString().equals(userid)) {
                            token = pair.getValue().toString();
                        }
                        iter.remove(); // avoids a ConcurrentModificationException
                    }

                    sendNotification(token);
                    onBackPressed();
                    Intent intent = new Intent(AddTask.this, Tasks.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(AddTask.this, "Only one user can be assigned to a task!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateSelectedUsers() {
        ListView selectedUse = (ListView) findViewById(R.id.selected_users_scroll_view);

        String[] listItems = new String[selectedUsers.size()];
        for (int i = 0; i < selectedUsers.size(); i++) {
            String user = selectedUsers.get(i);
            listItems[i] = user;
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems);
        selectedUse.setAdapter(adapter);
        selectedUse.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String entry = parent.getAdapter().getItem(position).toString();
                selectedUsers.remove(entry);
                updateSelectedUsers();
            }
        });
    }

    protected void clearSelectedConvText() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selected_conv_name", "");
        editor.commit();
    }

    protected void clearSelectedConvSelectedText() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("selected_conv", "");
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == dateView) {
            datePicker.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.house_settings:
                startActivity(new Intent(this, House_Settings.class));
                break;
            case R.id.activity_notifications:
                startActivity(new Intent(this, notifications.class));
                break;
            case R.id.add_conv_add:
                startActivity(new Intent(this, AddConversation.class));
                break;
            case R.id.activitys:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.activity_show_tasks:
                startActivity(new Intent(this, Tasks.class));
                break;
            case R.id.activity_room_deciding:
                startActivity(new Intent(this, RoomDecidingActivity.class));
                break;
            case R.id.change_activity:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadData(String house_num) {
        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("house_numbers").child(house_num).child("users");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersHash = (HashMap) dataSnapshot.getValue();
                Iterator it = usersHash.entrySet().iterator();
                ListView mListView = (ListView) findViewById(R.id.users_scroll_view);
                final ArrayList<String> users = new ArrayList<String>();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    String userid = pair.getKey().toString();
                    Iterator it2 = ((HashMap) pair.getValue()).entrySet().iterator();
                    while (it2.hasNext()) {
                        Map.Entry pair2 = (Map.Entry) it2.next();
                        if (pair2.getKey().toString().equals("name")) {
                            users.add(pair2.getValue().toString());
                            userID_userName.put(userid, pair2.getValue().toString());

                        }
                        if (pair2.getKey().toString().equals("token")) {

                            userID_token.put(userid, pair2.getValue().toString());

                        }
                        it2.remove();
                    }
                    it.remove(); // avoids a ConcurrentModificationException
                }

                String[] listItems = new String[users.size()];
                for (int i = 0; i < users.size(); i++) {
                    String user = users.get(i);
                    listItems[i] = user;
                }

                ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_1, listItems);
                mListView.setAdapter(adapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String entry = parent.getAdapter().getItem(position).toString();
                        selectedUsers.add(entry);
                        updateSelectedUsers();
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
    private void sendNotification(final String reg_token) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                    String user_name = preferences.getString("user_name", "");
                    dataJson.put("body","You have been assigned a task from: "+user_name);
                    dataJson.put("title","SmartHouse");
                    json.put("notification",dataJson);
                    json.put("to",reg_token);
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+ LEGACY_SERVER_KEY)
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String finalResponse = response.body().string();
                }catch (Exception e){
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();

    }

}

