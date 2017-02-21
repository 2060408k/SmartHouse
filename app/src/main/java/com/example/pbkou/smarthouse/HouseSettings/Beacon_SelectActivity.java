package com.example.pbkou.smarthouse.HouseSettings;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pbkou.smarthouse.HouseSettings.House_Settings;
import com.example.pbkou.smarthouse.MainActivity;
import com.example.pbkou.smarthouse.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Beacon_SelectActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BluetoothAdapter bleDev = null;
    private BluetoothLeScanner scanner = null;
    private ScanResultArrayAdapter scanAdapter = null;
    private Button toggleScan = null;
    private SharedPreferences preferences;

    // request ID for enabling Bluetooth
    private static final int REQUEST_ENABLE_BT = 1000;

    private boolean isScanning = false;
    private int scanMode = ScanSettings.SCAN_MODE_BALANCED;


    private int lastx = 0;

    // currently selected beacon, if any
    private BeaconInfo selectedBeacon = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon__select);

        preferences=PreferenceManager.getDefaultSharedPreferences(this);

        //set the toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        // create the adapter used to populate the list of beacons and attach it to the widget
        scanAdapter = new ScanResultArrayAdapter(this);
        final ListView scanResults = (ListView) findViewById(R.id.beacon_list);
        scanResults.setAdapter(scanAdapter);

        // set up a click handler which sets/updates the selected beacon
        scanResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // retrieve the adapter and get the selected item from it
                ScanResultArrayAdapter adapter = (ScanResultArrayAdapter) adapterView.getAdapter();
                BeaconInfo newSelectedBeacon = adapter.getItem(position);
                Log.i(TAG, "Tapped beacon " + newSelectedBeacon.name);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("selected_beacon",newSelectedBeacon.name+"/"+newSelectedBeacon.address);
                editor.commit();
                if(!isScanning)
                    return;
                selectedBeacon = newSelectedBeacon;
                onBackPressed();
            }

        });

        // set up a handler for taps on the start/stop scanning button
        toggleScan = (Button) findViewById(R.id.btnToggleScan);
        toggleScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                toggleScan();
            }

        });

        // retrieve the BluetoothManager instance and check if Bluetooth is enabled. If not the
        // user will be prompted to enable it and the response will be checked in onActivityResult
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleDev = bluetoothManager.getAdapter();
        if (bleDev == null || !bleDev.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        toggleScan();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop any in-progress scan and stop updating the graph if activity is paused
        stopScan();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if(resultCode != RESULT_OK) {
                Toast.makeText(this, "Bluetooth not enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth enabled successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopScan() {
        if(scanner != null && isScanning) {
            Toast.makeText(this, "Stopping BLE scan...", Toast.LENGTH_SHORT).show();
            isScanning = false;
            Log.i(TAG, "Scan stopped");
            scanner.stopScan(bleScanCallback);
        }

        selectedBeacon = null;
        toggleScan.setText("Start scanning");
        scanAdapter.clear();
    }

    private void toggleScan() {
        if(!isScanning)
            startScan();
        else
            stopScan();
    }

    private void startScan() {
        if(scanner == null) {
            scanner = bleDev.getBluetoothLeScanner();
            if(scanner == null) {
                // probably tried to start a scan without granting Bluetooth permission
                Toast.makeText(this, "Failed to start scan (BT permission granted?)", Toast.LENGTH_LONG).show();
                Log.w(TAG, "Failed to get BLE scanner instance");
                return;
            }
        }

        Toast.makeText(this, "Starting BLE scan...", Toast.LENGTH_SHORT).show();

        // clear old scan results
        scanAdapter.clear();

        List<ScanFilter> filters = new ArrayList<>();
        ScanSettings settings = new ScanSettings.Builder().setScanMode(scanMode).build();
        scanner.startScan(filters, settings, bleScanCallback);
        isScanning = true;
        toggleScan.setText("Stop scanning");
    }

    // class implementing BleScanner callbacks
    private ScanCallback bleScanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            final BluetoothDevice dev = result.getDevice();
            final int rssi = result.getRssi();

            if(dev != null && isScanning) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // retrieve device info and add to or update existing set of beacon data
                        String name = dev.getName();
                        String address = dev.getAddress();
                        scanAdapter.update(dev, address, name == null ? "Unnamed device" : name, rssi);
                    }

                });
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

    // simple class to hold data about a beacon
    private class BeaconInfo {
        public BluetoothDevice device;
        public String address;
        public String name;
        public int rssi;

        private static final int WINDOW_SIZE = 9;
        private int[] window = new int[WINDOW_SIZE];
        private int windowptr = 0;

        public BeaconInfo(BluetoothDevice device, String address, String name, int rssi) {
            this.device = device;
            this.address = address;
            this.name = name;
            this.rssi = rssi;
            for(int i=0;i<WINDOW_SIZE;i++)
                this.window[i] = rssi;
        }

        // called when a new scan result for this beacon is parsed
        public void updateRssi(int newRssi) {
            this.rssi = newRssi;
            window[windowptr] = newRssi;
            windowptr = (windowptr + 1) % WINDOW_SIZE;
        }

        // returns the latest raw RSSI reading for this beacon
        public double getRssi() {
            return this.rssi;
        }

        // returns a very simple moving average of the last WINDOW_SIZE
        // RSSI values received for this beacon
        public double getFilteredRssi() {
            double mean = 0.0;
            for(int i=0;i<WINDOW_SIZE;i++) {
                mean += window[i];
            }
            mean /= WINDOW_SIZE;
            return mean;
        }

        @Override
        public boolean equals(Object o) {
            // test if beacon objects are equal using their addresses
            if(o != null && o instanceof BeaconInfo) {
                BeaconInfo other = (BeaconInfo) o;
                if(other.address.equals(address))
                    return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            // as with equals() use addresses to test equality
            return address.hashCode();
        }
    }

    // basic adapter class to act as a data source for the list widget
    private class ScanResultArrayAdapter extends BaseAdapter {

        private final Context context;
        private final HashMap<String, BeaconInfo> data;
        private final List<String> keys;

        public ScanResultArrayAdapter(Context context) {
            super();
            this.context = context;
            this.keys = new ArrayList<>();
            this.data = new HashMap<>();
        }

        public void clear() {
            data.clear();
            keys.clear();
            notifyDataSetChanged();
        }

        @Override
        public BeaconInfo getItem(int position) {
            return data.get(keys.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        // updates the dataset with a new scan result. may create a new BeaconInfo object or update
        // an existing one.
        public void update(BluetoothDevice beaconDevice, String beaconAddress, String beaconName, int beaconRssi) {
            if(data.containsKey(beaconAddress)) {
                data.get(beaconAddress).updateRssi(beaconRssi);
            } else {
                BeaconInfo info = new BeaconInfo(beaconDevice, beaconAddress, beaconName, beaconRssi);
                data.put(beaconAddress, info);
                keys.add(beaconAddress);
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row;
            if(convertView == null) {
                row = inflater.inflate(R.layout.layout_scanresult, parent, false);
            } else {
                row = convertView;
            }

            // manually set the contents of each of the labels
            TextView field1 = (TextView) row.findViewById(R.id.resultField1);
            TextView field2 = (TextView) row.findViewById(R.id.resultField2);
            BeaconInfo info = data.get(keys.get(position));
            field1.setText(info.name + " [" + info.rssi + " dBm]");
            field2.setText(info.address);

            // if this happens to be the selected beacon, change the background colour to highlight it
            if(selectedBeacon != null && info.equals(selectedBeacon))
                row.setBackgroundColor(Color.argb(64, 0, 255, 0));
            else
                row.setBackgroundColor(Color.argb(255, 255, 255, 255));

            return row;
        }
    }
}
