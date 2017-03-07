package com.example.pbkou.smarthouse;

import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pbkou.smarthouse.Database.DBHandler;
import com.sendbird.android.shadow.com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alexiah on 28/02/2017.
 */
public class ViewOneConversation extends AppCompatActivity {

    private EditText chatText;
    private boolean side = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_one_conversation);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //initilise components
        final Button buttonSend = (Button) findViewById(R.id.send);
        //final ListView listView = (ListView) findViewById(R.id.msgview);
        final LinearLayout listView = (LinearLayout) findViewById(R.id.msgview);

        DBHandler dbhandler = new DBHandler(ViewOneConversation.this);
        dbhandler.printMessageTable();
        dbhandler.printMessageRecipientTable();
         //Get Group from previous activity
        String jsonMyObject = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            jsonMyObject = bundle.getString("group");
        }
        final Group group = new Gson().fromJson(jsonMyObject,Group.class);
        //manually add a message in this conversation
//        Message message = new Message("User1","Testing message 3","02/02/2017 - 19:00",group.getGroupID());
//        dbhandler.createMessage(message);
//        MessageRecipient msgRec = new MessageRecipient(group.getGroupID(), message.getId());
//        dbhandler.createMessageRecipient(msgRec);
//        Message message2 = new Message("User2","Testing message 4","02/02/2017 - 19:12",group.getGroupID());
//        dbhandler.createMessage(message2);
//        MessageRecipient msgRec2 = new MessageRecipient(group.getGroupID(), message2.getId());
//        dbhandler.createMessageRecipient(msgRec2);


        ArrayList<MessageRecipient> all_message_recipient = dbhandler.getMessagesOfGroup(group);

        TextView tv = new TextView(this);
        String content = "";
        for (MessageRecipient rec : all_message_recipient){
            Message msg = dbhandler.getMessageFromRecipient(rec.getMessage());
            System.out.println(msg.getBody());
            content+="\n"+ msg.getCreator() + "\n" + msg.getCreateDate() + "\n"+msg.getBody() + "\n";
        }
        tv.setText(content);
        tv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setTextSize(32);
        listView.addView(tv);


        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage(group);
                }
                return false;
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendChatMessage(group);
            }
        });

    }

    private boolean sendChatMessage(Group group) {
        DBHandler dbhandler = new DBHandler(getBaseContext());
        //Get current user name
        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String user = preferences.getString("user","");
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Message message = new Message("user",chatText.getText().toString(),date,group.getGroupID());
        dbhandler.createMessage(message);
        MessageRecipient msgRec = new MessageRecipient(group.getGroupID(), message.getId());
        dbhandler.createMessageRecipient(msgRec);
        finish();
        startActivity(getIntent());
        //chatText.setText("");
        //side = !side;
        return true;
    }
}
