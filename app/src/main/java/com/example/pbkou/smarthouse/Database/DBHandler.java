package com.example.pbkou.smarthouse.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.pbkou.smarthouse.Beacon;
import com.example.pbkou.smarthouse.Message;
import com.example.pbkou.smarthouse.MessageRecipient;
import com.example.pbkou.smarthouse.UserGroup;
import com.example.pbkou.smarthouse.Group;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Created by pbkou on 21/02/2017.
 */


public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 14;
    private static final String DATABASE_NAME = "beaconDB.db";
    private static final String TABLE_BEACONS = "beacons";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BEACONNAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_AREA = "area";

    /* Chat Required Databases */

    // Users table from Onlie Database
    // Message Table
    private static final String TABLE_MESSAGES = "messages";

    public static final String COLUMN_MESSAGE_ID = "id";
    public static final String COLUMN_CREATOR_ID = "creator";
    public static final String COLUMN_MESSAGE_BODY = "body";
    public static final String COLUMN_CREATE_DATE = "createDate";
    public static final String COLUMN_PARENT_ID = "parent";

    // Message_Recipient Table
    private static final String TABLE_MESS_RECIPIENT = "recipient";

    public static final String COLUMN_MESS_RES_ID = "recipientID";
    public static final String COLUMN_RECIP_GROUP = "groupID";
    public static final String COLUMN_MESS_ID = "messageID";

    // Group Table
    private static final String TABLE_GROUP = "group_tbl";

    public static final String COLUMN_GROUP_ID = "groupID";
    public static final String COLUMN_GROUP_NAME = "name";
    public static final String COLUMN_GROUP_CREATEDATE = "createDate";

    // User_Group Aggregate Table
    private static final String TABLE_USER_GROUP_AGGR = "user_group";

    public static final String COLUMN_UG_ID = "ugID";
    public static final String COLUMN_F_USER = "userF";
    public static final String COLUMN_F_GROUP = "groupF";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_BEACONS + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_BEACONNAME + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_AREA + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);

        String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_MESSAGES + "("
                + COLUMN_MESSAGE_ID + " TEXT PRIMARY KEY,"
                + COLUMN_CREATOR_ID + " TEXT,"
                + COLUMN_MESSAGE_BODY  + " TEXT,"
                + COLUMN_CREATE_DATE + " TEXT,"
                + COLUMN_PARENT_ID + " TEXT" + ")";
        db.execSQL(CREATE_MESSAGE_TABLE);

        String CREATE_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_GROUP + "("
                + COLUMN_GROUP_ID + " TEXT PRIMARY KEY,"
                + COLUMN_GROUP_NAME + " TEXT,"
                + COLUMN_GROUP_CREATEDATE + " TEXT" + ")";
        db.execSQL(CREATE_GROUP_TABLE);

        String CREATE_USER_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_USER_GROUP_AGGR + "("
                + COLUMN_UG_ID + " TEXT PRIMARY KEY,"
                + COLUMN_F_USER + " TEXT,"
                + COLUMN_F_GROUP + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_F_GROUP + ") REFERENCES "+ TABLE_GROUP + "("+COLUMN_GROUP_ID+")" + ")";
        db.execSQL(CREATE_USER_GROUP_TABLE);

        String CREATE_MESSAGE_RECIPIENT_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_MESS_RECIPIENT + "("
                + COLUMN_MESS_RES_ID + " TEXT PRIMARY KEY,"
                + COLUMN_RECIP_GROUP + " TEXT,"
                + COLUMN_MESS_ID + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_RECIP_GROUP + ") REFERENCES "+ TABLE_USER_GROUP_AGGR + "("+COLUMN_UG_ID+")"
                + "FOREIGN KEY(" + COLUMN_MESS_ID + ") REFERENCES "+ TABLE_MESSAGES + "("+COLUMN_MESSAGE_ID+")" + ")";
        db.execSQL(CREATE_MESSAGE_RECIPIENT_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_GROUP_AGGR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESS_RECIPIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        System.out.println("oldVersion: "+ oldVersion + " newVersion: "+newVersion);
        onCreate(db);
    }

    /**
     *Add new beacon to the database
     * @param beacon Beacon object
     */
    public void addBeacon(Beacon beacon) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, beacon.getId());
        values.put(COLUMN_BEACONNAME, beacon.getName());
        values.put(COLUMN_ADDRESS, beacon.getAddress());
        values.put(COLUMN_AREA, beacon.getArea());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_BEACONS, null, values);
        db.close();
    }

    /**
     * Find the beacon and retrieve it
     * @param beaconname the beacon name
     * @return
     */
    public Beacon findBeacon(String beaconname) {
        String query = "Select * FROM " + TABLE_BEACONS + " WHERE " + COLUMN_BEACONNAME + " =  \"" + beaconname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Beacon beacon = new Beacon();

        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            beacon.setId(cursor.getString(0));
            beacon.setName(cursor.getString(1));
            beacon.setAddress(cursor.getString(2));
            beacon.setArea(cursor.getString(3));
            cursor.close();
        } else {
            beacon = null;
        }

        db.close();
        return beacon;
    }

    /**
     * Search database for the beacon name and delete the entry
     * @param beaconname The beaco name
     * @return True if succeded, False otherwise
     */
    public boolean deleteBeacon(String beaconname) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_BEACONS + " WHERE " + COLUMN_BEACONNAME + " =  \"" + beaconname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Beacon beacon = new Beacon();

        if (cursor.moveToFirst()) {
            beacon.setId(cursor.getString(0));
            System.out.println(beacon.getId()+" delete");
            db.delete(TABLE_BEACONS, COLUMN_ID + " = ?",
                    new String[] { beacon.getId() });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    /**
     * Get all beacons from the database
     * @return ArrayList with the beacons
     */
    public ArrayList<Beacon> getAllBeacons(){
        String query = "Select * FROM " + TABLE_BEACONS ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Beacon> out = new ArrayList<Beacon>();

        try {
            while (cursor.moveToNext()) {
                Beacon beacon = new Beacon();
                System.out.println(cursor.getString(0)+" getall");
                beacon.setId(cursor.getString(0));
                beacon.setName(cursor.getString(1));
                beacon.setAddress(cursor.getString(2));
                beacon.setArea(cursor.getString(3));
                out.add(beacon);
            }
        } finally {
            cursor.close();
        }

        return out;
    }


    public void resetBeaconTable() {
        String query = "DELETE FROM " + TABLE_BEACONS;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
    }

    /**
     * Create a new group
     * @param group Group object
     */
    public void createGroup(Group group) {
        System.out.println(DATABASE_VERSION);
        ContentValues values = new ContentValues();
        values.put(COLUMN_GROUP_ID, group.getGroupID());
        values.put(COLUMN_GROUP_NAME, group.getName());
        values.put(COLUMN_GROUP_CREATEDATE, group.getCreateDate());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_GROUP, null, values);
        db.close();
    }
    /**
     * Create a new user group instance
     * @param userG UserGroup object
     */
    public void createUserGroup(UserGroup userG) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_UG_ID, userG.getId());
        values.put(COLUMN_F_USER, userG.getUser());
        values.put(COLUMN_F_GROUP, userG.getGroup());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_USER_GROUP_AGGR, null, values);
        db.close();

    }

    /**
     * Create a new message recipient instance
     * @param messRec messageRecipient object
     */
    public void createMessageRecipient(MessageRecipient messRec) {

        ContentValues values = new ContentValues();
        values.put(COLUMN_MESS_RES_ID, messRec.getId());
        values.put(COLUMN_RECIP_GROUP, messRec.getGroup());
        values.put(COLUMN_MESS_ID, messRec.getMessage());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_MESS_RECIPIENT, null, values);
        db.close();

    }

    /**
     * Create a new message recipient instance
     * @param message messageRecipient object
     */
    public void createMessage(Message message) {
        SQLiteDatabase db1 = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_ID, message.getId());
        values.put(COLUMN_CREATOR_ID, message.getCreator());
        values.put(COLUMN_MESSAGE_BODY, message.getBody());
        values.put(COLUMN_CREATE_DATE, message.getCreateDate());
        System.out.println("Message Date: "+ message.getCreateDate());

        values.put(COLUMN_PARENT_ID, message.getParent());

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(TABLE_MESSAGES, null, values);
        db.close();

    }

    /**
     * Get all groups (conversations) from the database
     * @return ArrayList with the groups (conversations)
     */
    public ArrayList<String> getUsersOfGroup(Group g){
        String query = "Select * FROM " + TABLE_USER_GROUP_AGGR ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<String> out = new ArrayList<String>();

        try {
            while (cursor.moveToNext()) {
                UserGroup userg = new UserGroup();
                userg.setId(cursor.getString(0));
                userg.setGroup(cursor.getString(1));
                userg.setUser(cursor.getString(2));
                if (userg.getGroup().equals(g.getGroupID()) ){
                    String user = userg.getUser();
                    out.add(user);
                }

            }
        } finally {
            cursor.close();
        }

        return out;
    }

    /**
     * Get all groups (conversations) from the database
     * @return ArrayList with the groups (conversations)
     */
    public ArrayList<Map<Group, ArrayList>> getAllConversations(){
        String query = "Select * FROM " + TABLE_GROUP ;

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        ArrayList<Map<Group, ArrayList>> out = new ArrayList<>();

        try {
            while (cursor.moveToNext()) {
                Group group = new Group();
                group.setGroupID(cursor.getString(0));
                group.setName(cursor.getString(1));
                ArrayList Users = getUsersOfGroup(group);
                Map<Group, ArrayList> map = new HashMap<Group, ArrayList>();
                map.put(group, Users);
                out.add(map);
            }
        } finally {
            cursor.close();
        }
        return out;
    }

    public boolean deleteGroup(Group group) {

        boolean result = false;
        System.out.println("***************************************************************");
        //Delete all user group instances of the particular group
        String query = "Select * FROM " + TABLE_USER_GROUP_AGGR + " WHERE " + COLUMN_F_GROUP + " =  \"" + group + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        UserGroup userg = new UserGroup();

        if (cursor.moveToFirst()) {
            userg.setId(cursor.getString(0));
            System.out.println(userg.getId()+" delete");
            db.delete(TABLE_USER_GROUP_AGGR, COLUMN_UG_ID + " = ?",
                    new String[] { userg.getId() });
            cursor.close();
            result = true;
        }
        db.close();
        return result;
    }

    public void DeleteMessagesOfGroup(){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("delete from "+ TABLE_MESSAGES);
        db.execSQL("delete from "+ TABLE_MESS_RECIPIENT);

    }
    public ArrayList<MessageRecipient> getMessagesOfGroup(Group g){
        String query = "Select * FROM " + TABLE_MESS_RECIPIENT +" Where groupID= \""+ g.getGroupID()+"\"";

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<MessageRecipient> msRec = new ArrayList<MessageRecipient>();
        Cursor cursor = db.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                MessageRecipient msgR = new MessageRecipient();
                msgR.setId(cursor.getString(0));
                msgR.setGroup(cursor.getString(1));
                msgR.setMessage(cursor.getString(2));
                System.out.println("Message ID from getMessagesOfGroup: "+msgR.getMessage());
                msRec.add(msgR);
            }
        } finally {
            cursor.close();
        }
        return msRec;
    }

    public void printMessageTable(){
        String query = "Select * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                Message message = new Message();
                message.setId(cursor.getString(0));
                message.setCreator(cursor.getString(1));
                message.setBody(cursor.getString(2));
                message.setCreateDate(cursor.getString(3));
                message.setParent(cursor.getString(4));
                System.out.println("Message Table Content: " + message.getBody() );
            }
        } finally {
            cursor.close();
        }
    }

    public void printMessageRecipientTable(){
        String query = "Select * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        try {
            while (cursor.moveToNext()) {
                MessageRecipient msgR = new MessageRecipient();
                msgR.setId(cursor.getString(0));
                msgR.setGroup(cursor.getString(1));
                msgR.setMessage(cursor.getString(2));
                System.out.println("Message Recipient Table Content: -Group: " + msgR.getGroup() +"- Message: "+ msgR.getMessage() );
            }
        } finally {
            cursor.close();
        }
    }
    public Message getMessageFromRecipient(String msgR) {

        String query = "Select * FROM " + TABLE_MESSAGES + " Where id= \"" + msgR + "\"";
        SQLiteDatabase db = this.getWritableDatabase();
        Message message = new Message();
        Cursor cursor = db.rawQuery(query, null);
        try {
            while (cursor.moveToNext()) {
                message = new Message();
                message.setId(cursor.getString(0));
                message.setCreator(cursor.getString(1));
                message.setBody(cursor.getString(2));
                message.setCreateDate(cursor.getString(3));
                message.setParent(cursor.getString(4));
            }
        } finally {
            cursor.close();
        }
        return message;
    }

}
