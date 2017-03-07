package com.example.pbkou.smarthouse;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alexiah on 03/03/2017.
 */

public class AddTask  extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences preferences;
    private DatabaseReference mDatabase;
    final ArrayList<String> selectedUsers = new ArrayList<String>();
    private TextView dateView;
    private Calendar calendar;
    private DatePickerDialog datePicker;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        //Get the preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

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

        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        //Populate List of users
        ListView mListView = (ListView) findViewById(R.id.users_scroll_view);
        final ArrayList<String> users = new ArrayList<String>();
        users.add("User1");
        users.add("User2");
        users.add("User3");
        String[] listItems = new String[users.size()];
        for (int i=0; i<users.size(); i++){
            String user = users.get(i);
            listItems[i] = user;
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listItems);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String entry = parent.getAdapter().getItem(position).toString();
                selectedUsers.add(entry);
                updateSelectedUsers();
            }
        });

        //add btn
        Button add_btn= (Button) findViewById(R.id.add_task_btn);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear the conv text
                clearSelectedConvText();
                DBHandler dbhandler = new DBHandler(getBaseContext());
                Task addTask = new Task(selectedUsers.get(0),dateView.getText().toString(),task_body_txt.getText().toString());

                boolean multipleUsers= false;
                if (selectedUsers.size()>1){
                    multipleUsers=true;
                }
                if (!multipleUsers ){
                    dbhandler.createTask(addTask);
                    onBackPressed();
                    Intent intent = new Intent(AddTask.this, Tasks.class );
                    startActivity(intent);
                }else{
                    if(multipleUsers)
                        Toast.makeText(AddTask.this,"Only one user can be assigned!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    private void updateSelectedUsers() {
        ListView selectedUse = (ListView) findViewById(R.id.selected_users_scroll_view);

        String[] listItems = new String[selectedUsers.size()];
        for (int i=0; i<selectedUsers.size(); i++){
            String user = selectedUsers.get(i);
            listItems[i] = user;
        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listItems);
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
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("selected_conv_name","");
        editor.commit();
    }
    protected void clearSelectedConvSelectedText() {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("selected_conv","");
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public void onClick(View view){
        if (view==dateView){
            datePicker.show();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.house_settings:
                Intent intent = new Intent(this,House_Settings.class);
                startActivity(intent);
                break;
            case R.id.view_conversations:
                Intent intent2 = new Intent(this,Conversations.class);
                startActivity(intent2);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
