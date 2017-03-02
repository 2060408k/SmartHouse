package com.example.pbkou.smarthouse;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.HouseSettings.Add_BeaconActivity;
import com.example.pbkou.smarthouse.HouseSettings.Beacon_SelectActivity;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sendbird.android.shadow.com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by Alexiah on 02/03/2017.
 */

public class AddConversation extends AppCompatActivity {

    private SharedPreferences preferences;

    private String selectedMember="";
    private Context context;
    private EditText area;
    private DatabaseReference mDatabase;
    final ArrayList<String> selectedUsers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_converation);

        //Get the preference manager
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        //set the ConvName editext
        final EditText conv_name_txt = (EditText) findViewById(R.id.add_conv_name);

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
        Button add_btn= (Button) findViewById(R.id.add_conv_add);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear the conv text
                clearSelectedConvText();
                DBHandler dbhandler = new DBHandler(getBaseContext());
                String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Group addgroup = new Group(conv_name_txt.getText().toString(),date);

                boolean conv_already_exists= false;
                for (Group g :dbhandler.getAllConvNoUsers()){
                    if (g.getName().equals(addgroup.getName())) conv_already_exists=true;
                }
                if (!conv_already_exists ){
                    dbhandler.createGroup(addgroup);
                    int x=0;
                    for (x=0; x<selectedUsers.size(); x++){
                        UserGroup userGroup = new UserGroup(addgroup.getGroupID(),selectedUsers.get(x));
                        dbhandler.createUserGroup(userGroup);
                    }
                    onBackPressed();
                    Intent intent = new Intent(AddConversation.this, Conversations.class );
                    startActivity(intent);
                }else{
                    if(conv_already_exists)
                        Toast.makeText(AddConversation.this,"Conversation name already exists",Toast.LENGTH_LONG).show();
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
    public void onBackPressed() {
        clearSelectedConvText();
        clearSelectedConvSelectedText();
        super.onBackPressed();

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

    public void onDestroy() {
        clearSelectedConvText();
        clearSelectedConvSelectedText();
        super.onDestroy();

    }


    //Getters-Setters

    public EditText getArea() {
        return area;
    }

    public void setArea(EditText area) {
        this.area = area;
    }
}


