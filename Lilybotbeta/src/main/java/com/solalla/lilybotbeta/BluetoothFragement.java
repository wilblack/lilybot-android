package com.solalla.lilybotbeta;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by wilblack on 11/13/13.
 */
class BluetoothFragment extends Fragment implements View.OnClickListener {

    Context mContext;
    static String TAG = "Bluetooth Fragment";
    TextView tvStatus;
    View rootView;

    public ArrayList<String> devices;
    public ArrayList<BluetoothDevice> btDevices;
    public IntentFilter filter;

    public void PlaceholderFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity().getApplicationContext();


        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        devices = new ArrayList<String>();
        btDevices = new ArrayList<BluetoothDevice>();


        // Register filters
        filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter filterStart = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        IntentFilter filterStop = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);
        mContext.registerReceiver(mReceiver, filterStart);
        mContext.registerReceiver(mReceiver, filterStop);

        // Add button listeners
        Button scanButton = (Button) rootView.findViewById(R.id.bluetoothScanBtn);
        scanButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bluetoothScanBtn:
                findBluetoothDevices();

                break;
        }

    }
    public void findBluetoothDevices() {
        Log.d(TAG, "Starting to search for bluetooth devices");
        int REQUEST_ENABLE_BT = 0;

        //check for bluetooth
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "No bluetooth adapter");
        } else {
            Log.d(TAG,"Bluetooth adaptor found");
            if (!mBluetoothAdapter.isEnabled()) {
                Log.d(TAG,"Bluetooth is not enabled");
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                Log.d(TAG,"Bluetooth is enabled");
                mBluetoothAdapter.startDiscovery();

            }
        }
    }

    public void btConnect(BluetoothDevice device){
        tvStatus.setText("Attempting to connect to "+ device.toString() );

    }


    // Create a BroadcastReceiver for ACTION_FOUND for Bluetooth
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        ArrayAdapter<String> devicesAdapter;
        ListView listView;

        public void onReceive(Context context, Intent intent) {

            Log.d(TAG,"In mReceiver()");
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                String text;

                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                // Add the name and address to an array adapter to show in a ListView
                text = device.getName() + "\n" + device.getAddress();
                Log.d(TAG,text );
                devices.add(device.getName() + "\n" + device.getAddress());
                btDevices.add(device);


                if (devicesAdapter == null){
                    // Render the results in List
                    listView = (ListView) rootView.findViewById(R.id.devicesListView);
                    // Update adaptor with new results
                    devicesAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, devices);
                    listView.setAdapter(devicesAdapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
                            btConnect(btDevices.get(position));
                        }
                    });

                } else {
                    devicesAdapter.notifyDataSetChanged();
                }


            }
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Log.d(TAG, "Started discovery");

                // Clear previous list
                if (devicesAdapter != null) {
                    devices.clear();
                    btDevices.clear();
                    devicesAdapter.notifyDataSetChanged();
                }

                tvStatus = (TextView)rootView.findViewById(R.id.status);
                tvStatus.setText("Scanning...");


            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.d(TAG, "Finished discovery");
                tvStatus = (TextView)rootView.findViewById(R.id.status);
                tvStatus.setText("Stopped");


            }
        }
    };

}  //End PlaceHolder Fragment