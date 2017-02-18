package com.example.pbkou.smarthouse;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by pbkou on 03/02/2017.
 */

// basic adapter class to act as a data source for the list widget
public class ScanResultArrayAdapter extends BaseAdapter {

    private final Context context;
    private final HashMap<String, BeaconInfo> data;
    private final List<String> keys;
    // currently selected beacon, if any
    private BeaconInfo selectedBeacon = null;

    public ScanResultArrayAdapter(Context context,BeaconInfo selectedBeacon) {
        super();
        this.context = context;
        this.keys = new ArrayList<>();
        this.data = new HashMap<>();
        this.selectedBeacon =selectedBeacon;
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
        System.out.println("SRAA update");
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
        System.out.println("meow "+row);
        return row;
    }
}
