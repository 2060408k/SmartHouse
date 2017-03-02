package com.example.pbkou.smarthouse;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pbkou.smarthouse.Database.DBHandler;
import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.example.pbkou.smarthouse.HouseSettings.ViewAllBeacons;
import com.sendbird.android.shadow.com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Alexiah on 23/02/2017.
 */

public class Conversations extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_conversations);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final LinearLayout main_view = (LinearLayout) findViewById(R.id.conversations_scroll_view);

        final FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.btn_create_conv);
        final Intent intent = new Intent(this,Conversations.class);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });

        DBHandler dbhandler = new DBHandler(getBaseContext());

        //Uncomment this to manually add a converstation
////        /* Insert test data - One Conversation with 2 users */
//        Group testGroup = new Group("testGroup3","25/02/2017");
//        dbhandler.createGroup(testGroup);
//        UserGroup userGroup1 = new UserGroup(testGroup.getGroupID(),"User1");
//        UserGroup userGroup2 = new UserGroup(testGroup.getGroupID(),"User2");
//        dbhandler.createUserGroup(userGroup1);
//        dbhandler.createUserGroup(userGroup2);
//        System.out.println(testGroup);
//        System.out.println(userGroup1);
//        System.out.println(userGroup2);

        ArrayList<Map<Group, ArrayList>> all_conversations = dbhandler.getAllConversations();

        int index=1;
        for (Map<Group, ArrayList> conv : all_conversations) {
            final Map<Group, ArrayList> temp_group = conv;
            for (final Map.Entry<Group, ArrayList> entry : temp_group.entrySet()) {
//                System.out.println(entry);
//                System.out.println(entry.getKey().getName() + "temp");
                TextView tv = new TextView(this);
                String content = "Conversation "+ index + "\n" + entry.getKey().getName() + "\n";
                for (int i = 0; i < entry.getValue().size(); i++) {
                    content += entry.getValue().get(i);
                    if (i != entry.getValue().size()-1){
                        content += ", ";
                    }
                }
                index++;
                tv.setText(content);
                tv.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));
                tv.setTextSize(32);
                //TODO: DELETE IS NOT WORKING AT THE MOMENT
                tv.setOnLongClickListener(new View.OnLongClickListener(){

                    @Override
                    public boolean onLongClick(View v) {

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(Conversations.this);

                        final TextView et = new TextView(Conversations.this);
                        et.setText(R.string.delete_conversation);
                        et.setTextSize(28);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(et);

                        // set dialog message
                        alertDialogBuilder.setCancelable(true).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                System.out.println("---11111------------------");

                                DBHandler dbhandler = new DBHandler(Conversations.this);
                                System.out.println("------------------------------");
                                for (Map.Entry<Group, ArrayList> entry : temp_group.entrySet()) {
                                    System.out.println(entry);
                                    System.out.println(dbhandler.deleteGroup(entry.getKey()));
                                }

                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                                //dialog.dismiss();

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        // create alert dialog
                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                        return false;
                    }
                });
                tv.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Intent view_conv = new Intent(v.getContext(), ViewOneConversation.class);
                        view_conv.putExtra("group", new Gson().toJson(entry.getKey()));
                        startActivity(view_conv);
                        }
                });
                main_view.addView(tv);
            }
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
                Intent intent = new Intent(this,House_Settings.class);
                startActivity(intent);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}



