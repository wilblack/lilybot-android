package com.solalla.lilybotbeta;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.OnClickListener;

public class MainActivity extends Activity {

    static String TAG = "Main Activity";
    public ArrayList<String> devices;

    public IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(mReceiver);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public class PlaceholderFragment extends Fragment implements OnClickListener {

        TextView tvStatus;
        View rootView;
        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.fragment_main, container, false);
            devices = new ArrayList<String>();

            // Register filters
            filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            IntentFilter filterStart = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
            IntentFilter filterStop = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mReceiver, filter);
            registerReceiver(mReceiver, filterStart);
            registerReceiver(mReceiver, filterStop);

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

        // Create a BroadcastReceiver for ACTION_FOUND for Bluetooth
        private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

            public void onReceive(Context context, Intent intent) {

                Log.d(TAG,"In mReceiver()");
                String action = intent.getAction();

                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    // Add the name and address to an array adapter to show in a ListView
                    Log.d(TAG,device.getName() + "\n" + device.getAddress() );
                    devices.add(device.getName() + "\n" + device.getAddress());

                }
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    Log.d(TAG, "Started discovery");
                    tvStatus = (TextView)rootView.findViewById(R.id.status);
                    tvStatus.setText("Scanning...");
                }
                if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.d(TAG, "Finished discovery");
                    tvStatus = (TextView)rootView.findViewById(R.id.status);
                    tvStatus.setText("Stopped");

                    // Render the results in List
                    ListView listView = (ListView) rootView.findViewById(R.id.devicesListView);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, devices);
                    listView.setAdapter(adapter);

                }
            }
        };

    }  //End PlaceHolder Fragment










}
