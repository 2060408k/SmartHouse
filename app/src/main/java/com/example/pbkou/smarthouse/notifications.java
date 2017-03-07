package com.example.pbkou.smarthouse;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.pbkou.smarthouse.Database.LoginActivity;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;

public class notifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.house_settings:
                startActivity(new Intent(this, House_Settings.class));
                break;
            case R.id.view_conversations:
                startActivity(new Intent(this, Conversations.class));
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
}
