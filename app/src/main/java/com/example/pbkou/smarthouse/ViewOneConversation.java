package com.example.pbkou.smarthouse;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pbkou.smarthouse.Database.DBHandler;
import com.sendbird.android.shadow.com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Alexiah on 28/02/2017.
 */
public class ViewOneConversation extends AppCompatActivity {

    private ChatArrayAdapter chatArrayAdapter;
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
        final ListView listView = (ListView) findViewById(R.id.msgview);
//        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_message);
//        listView.setAdapter(chatArrayAdapter);

        DBHandler dbhandler = new DBHandler(ViewOneConversation.this);

        // Get Group from previous activity
        String jsonMyObject = null;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            jsonMyObject = bundle.getString("group");
            System.out.println("I am in the bundle object");
        }
        Group group = new Gson().fromJson(jsonMyObject,Group.class);
        System.out.println("********* Group: "+ group.getName());


        ArrayList<Message> all_messages = dbhandler.getMessagesOfGroup(group);
        for (Message msg : all_messages){

            TextView tv = new TextView(this);
            tv.setText(msg.getCreateDate() + "\n" + msg.getCreator() + "\n"+msg.getBody() + "\n" );
            tv.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setTextSize(32);

            listView.addView(tv);
        }

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return (event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER) && sendChatMessage();
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    private boolean sendChatMessage() {
        DBHandler dbhandler = new DBHandler(getBaseContext());
        Date date = new Date();
        Message message = new Message("testUser",chatText.getText().toString(),date,"GroupTesting");
        dbhandler.createMessage(message);
        MessageRecipient msgRec = new MessageRecipient("GroupTesting", message.getId());
        dbhandler.createMessageRecipient(msgRec);

        chatArrayAdapter.add(new ChatMessage(side, message));
        chatText.setText("");
        side = !side;
        return true;
    }
}
