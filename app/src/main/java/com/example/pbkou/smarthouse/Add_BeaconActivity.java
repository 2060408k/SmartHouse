package com.example.pbkou.smarthouse;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Add_BeaconActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beacon_);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //select beacon btn
        Button select_beacon_btn =  (Button) findViewById(R.id.add_beacon_select_beacon);
        final Intent select_beacon_intent = new Intent(this,Beacon_SelectActivity.class);
        select_beacon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(select_beacon_intent);
            }
        });

        //set the BeaconName editext
        EditText beacon_name_txt = (EditText) findViewById(R.id.add_beacon_beacon_name);

        //check if we selected a beacon name
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String beacon_selected_name = sharedPref.getString("selected_house_beacon_name","");
        if(!beacon_selected_name.isEmpty()) beacon_name_txt.setText(beacon_selected_name);

        //add a text changed listener
        beacon_name_txt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("selected_house_beacon_name",s.toString());
                editor.commit();
            }
        });

        //add btn
        Button add_btn= (Button) findViewById(R.id.add_beacon_add);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSelectedBeaconText();
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
    public void onResume(){
        //set beacon selected tv
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println("resuming");
        TextView beacon_selected_tv = (TextView) findViewById(R.id.add_beacon_selected_beacon_name);
        String beacon_selected = sharedPref.getString("selected_beacon","");
        if(!beacon_selected.isEmpty()) beacon_selected_tv.setText(beacon_selected);
        System.out.println(beacon_selected+" meow");
        super.onResume();

    }
    @Override
    public void onBackPressed() {
        clearSelectedBeaconText();
        clearSelectedBeaconSelectedText();
        super.onBackPressed();

    }

    protected void clearSelectedBeaconText() {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("selected_house_beacon_name","");
        editor.commit();
    }
    protected void clearSelectedBeaconSelectedText() {
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("selected_beacon","");
        editor.commit();
    }
}
