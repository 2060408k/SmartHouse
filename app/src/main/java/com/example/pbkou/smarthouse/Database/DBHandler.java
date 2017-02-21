package com.example.pbkou.smarthouse.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.pbkou.smarthouse.Beacon;

import java.util.ArrayList;

/**
 * Created by pbkou on 21/02/2017.
 */

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "beaconDB.db";
    private static final String TABLE_BEACONS = "beacons";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BEACONNAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_AREA = "area";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_PRODUCTS_TABLE = "CREATE TABLE " +
                TABLE_BEACONS + "("
                + COLUMN_ID + " TEXT PRIMARY KEY,"
                + COLUMN_BEACONNAME + " TEXT,"
                + COLUMN_ADDRESS + " TEXT,"
                + COLUMN_AREA + " TEXT" + ")";
        db.execSQL(CREATE_PRODUCTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEACONS);
        onCreate(db);
    }

    /**
     *Add new beacon to the database
     * @param beacon Beacon object
     */
    public void addBeacon(Beacon beacon) {

        ContentValues values = new ContentValues();
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
    public boolean deleteProduct(String beaconname) {

        boolean result = false;

        String query = "Select * FROM " + TABLE_BEACONS + " WHERE " + COLUMN_BEACONNAME + " =  \"" + beaconname + "\"";

        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        Beacon beacon = new Beacon();

        if (cursor.moveToFirst()) {
            beacon.setId(cursor.getString(0));
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
}
