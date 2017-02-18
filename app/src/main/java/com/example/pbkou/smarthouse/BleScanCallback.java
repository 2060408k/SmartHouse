package com.example.pbkou.smarthouse;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.Log;

import java.util.List;

/**
 * Created by pbkou on 03/02/2017.
 */

public class BleScanCallback extends ScanCallback {

    private boolean isScanning;
    private ScanResultArrayAdapter scanAdapter = null;
    private static final String TAG = MainActivity.class.getSimpleName();

    public BleScanCallback(boolean isScanning,ScanResultArrayAdapter scanAdapter){
        super();
        this.isScanning=isScanning;
        this.scanAdapter=scanAdapter;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);

        final BluetoothDevice dev = result.getDevice();
        final int rssi = result.getRssi();

        if(dev != null && isScanning) {
            // retrieve device info and add to or update existing set of beacon data
            String name = dev.getName();
            String address = dev.getAddress();
            scanAdapter.update(dev, address, name == null ? "Unnamed device" : name, rssi);

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
}
