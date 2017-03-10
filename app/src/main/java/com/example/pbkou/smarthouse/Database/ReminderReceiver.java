package com.example.pbkou.smarthouse.Database;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.pbkou.smarthouse.Beacon;
import com.example.pbkou.smarthouse.HouseSettings.ViewAllBeacons;
import com.example.pbkou.smarthouse.MainActivity;
import com.example.pbkou.smarthouse.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pbkou on 07/03/2017.
 */

public class ReminderReceiver extends BroadcastReceiver {

    private String user_name;
    private String user_id;
    private String house_num;
    private String location;
    private BluetoothAdapter bleDev = null;
    private BluetoothLeScanner scanner = null;
    private int scanMode = ScanSettings.SCAN_MODE_BALANCED;
    private boolean isScanning = false;
    private HashMap<String,Integer> active_beacon_list = new HashMap<String,Integer>();
    private static final String TAG = MainActivity.class.getSimpleName();
    private AlarmManager alarmMgr;
    private Context context;
    // request ID for enabling Bluetooth
    private static final int REQUEST_ENABLE_BT = 1000;
    private SharedPreferences preferences;
    CurrentLocationService currentLocationService = new CurrentLocationService();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private String[] current_beacon={" ", "-1000"};
    public ReminderReceiver(){


    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context=context;
        //start scanning repeatedly every 30 seconds
        startRepeatingPattern();

    }

    public void startRepeatingPattern(){
        Timer myTimer = new Timer();
        myTimer.scheduleAtFixedRate(new BScanner(), 0, 30000);
    }
    public class BScanner extends TimerTask {
        public void run() {
            startScanningBles();
        }
    }

    public void startScanningBles(){

        //get bluetooth manager
        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bleDev = bluetoothManager.getAdapter();

        //get bluetooth permissions
        if (bleDev != null && bleDev.isEnabled()) {
            //set a bluetoothscanner
            if(scanner == null) {
                scanner = bleDev.getBluetoothLeScanner();
                if(scanner == null) {
                    // probably tried to start a scan without granting Bluetooth permission
                    return;
                }
            }

            //start scanning
            isScanning = true;
            List<ScanFilter> filters = new ArrayList<>();
            ScanSettings settings = new ScanSettings.Builder().setScanMode(scanMode).build();
            scanner.startScan(filters, settings, bleScanCallback);

            //give 20 seconds for the scanner to look for beacons
            Thread t=new Thread()
            {
                public void run()
                {
                    try{
                        sleep(1000*20);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        stopScan();
                    }
                }
            };
            t.start();
        }else {
            stopScan();
        }




    }

    private void stopScan() {
        //stop the scanner
        if(scanner != null && isScanning) {
            scanner.stopScan(bleScanCallback);
            isScanning=false;
        }

        //get database and all beacons
        DBHandler dbhandler = new DBHandler(context);
        ArrayList<Beacon> beacons =dbhandler.getAllBeacons();

        //iterate beacons
        for (Beacon beacon : beacons){
            //check if we found a beacon with same address
            if (active_beacon_list.containsKey(beacon.getAddress())){
                //update the current beacon
                if (current_beacon==null) {
                    current_beacon[0]=beacon.getAddress();
                    current_beacon[1]=active_beacon_list.get(beacon.getAddress()).toString();

                }else{
                    if(active_beacon_list.get(beacon.getAddress())>Integer.parseInt(current_beacon[1])){
                        current_beacon[0]=beacon.getArea();
                        current_beacon[1]=active_beacon_list.get(beacon.getAddress()).toString();
                    }
                }

            }
        }
        //get users data
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        user_id=preferences.getString("user","");
        user_name=preferences.getString("user_name","");
        house_num=preferences.getString("house_num","");

        //make sure data  are not empty
        if(user_name.isEmpty() || user_id.isEmpty() || house_num.isEmpty() ) return;

        //if we didn't update the current beacon then we are absent
        if(current_beacon[0].equals(" ")) current_beacon[0]="Absent";

        //if it's absent check if we have location data
        if (current_beacon[0].equals("Absent")){

            String latitude = preferences.getString("latitude_dbl","");
            String longitude = preferences.getString("longitude_dbl","");
            System.out.println("lat :"+latitude+" long : "+longitude);
            //if we have add them
            if(!latitude.isEmpty() && !longitude.isEmpty()){
                current_beacon[0]=latitude+"_"+longitude;
            }

        }

        //add result
        mDatabase.child("house_numbers").child(house_num).child("users").child(user_id).child("location").setValue(current_beacon[0]);


    }

    // class implementing BleScanner callbacks
    private ScanCallback bleScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            final BluetoothDevice dev = result.getDevice();
            final int rssi = result.getRssi();

            if(dev != null && isScanning) {
                String name = dev.getName();
                String address = dev.getAddress();
                if(!active_beacon_list.containsKey(address)) active_beacon_list.put(address,new Integer(rssi));
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d(TAG, "BatchScanResult(" + results.size() + " results)");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.w(TAG, "ScanFailed(" + errorCode + ")");
        }
    };
    public void toggleScan(){
        isScanning=!isScanning;
    }
}
