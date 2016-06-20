package com.example.andy.solenergi;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A placeholder fragment containing a simple view.
 */
public class SolenergiFragment extends Fragment {

    private static final String TAG = "BluetoothChatFragment";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private TextView voltageView;
    private TextView batVoltageView;
    private TextView currentView;
    private TextView powerView;

    // variables
    private double voltage;
    private double batVoltage;
    private double current;
    private double power;

    // graphing variables
    private double graphLastXValue = 0d;
    private LineGraphSeries<DataPoint> series;
    private double graph2LastXValue = 0d;
    private double graph3LastXValue = 0d;
    private LineGraphSeries<DataPoint> series2;
    private LineGraphSeries<DataPoint> series3;

    /**
     * Name of the connected device
     */
    private String mConnectedDeviceName = null;

    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;

    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothService mChatService = null;

    public SolenergiFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            FragmentActivity activity = getActivity();
            Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            activity.finish();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupBt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_solenergi, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        voltageView = (TextView) view.findViewById(R.id.voltage_text);
        batVoltageView = (TextView) view.findViewById(R.id.bat_voltage_text);
        currentView = (TextView) view.findViewById(R.id.current_text);
        powerView = (TextView) view.findViewById(R.id.power_text);


        GraphView graph = (GraphView) view.findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>();
        graph.addSeries(series);
        graph.getGridLabelRenderer().setGridColor(Color.GRAY);
        series2 = new LineGraphSeries<DataPoint>();
        graph.addSeries(series2);
        series3 = new LineGraphSeries<DataPoint>();
        graph.getSecondScale().addSeries(series3);

        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.GRAY);
        graph.getGridLabelRenderer().setVerticalLabelsColor(Color.GRAY);

        graph.getSecondScale().setMinY(0);
        graph.getSecondScale().setMaxY(30);

        series2.setColor(Color.YELLOW);
        series.setColor(Color.MAGENTA);
        series3.setColor(Color.CYAN);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(10);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(15);
        graph.getGridLabelRenderer().setPadding(30);
        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph.getGridLabelRenderer().setNumVerticalLabels(16);
        graph.getGridLabelRenderer().setVerticalAxisTitle("Volts/Amps");
        //graph.getGridLabelRenderer().set
        graph.getGridLabelRenderer().setVerticalAxisTitleColor(Color.GRAY);
        graph.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.GRAY);
        graph.getGridLabelRenderer().reloadStyles();
    }

    private void setupBt() {
        Log.d(TAG, "setupBt()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(getActivity(), mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * Makes this device discoverable.
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Updates the status on the action bar.
     *
     * @param resId a string resource ID
     */
    private void setStatus(int resId) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null == activity) {
            Log.v(TAG,"activity null");
            return;
        }
        final android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
        if (null == actionBar) {
            Log.v(TAG,"actionbar null");
            return;
        }
        actionBar.setSubtitle(resId);
    }

    /**
     * Updates the status on the action bar.
     *
     * @param subTitle status
     */
    private void setStatus(CharSequence subTitle) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (null == activity) {
            Log.v(TAG, "activity null");
            return;
        }
        final android.support.v7.app.ActionBar actionBar = activity.getSupportActionBar();
        if (null == actionBar) {
            Log.v(TAG,"actionbar null");

            return;
        }
        actionBar.setSubtitle(subTitle);
    }

    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = getActivity();
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    break;
                case Constants.MESSAGE_READ:
                    JSONObject inputJson = null;
                    try {
                        inputJson = new JSONObject((String) msg.obj);           //extract JSON string from msg

                        voltage = Double.parseDouble(inputJson.getString("voltage"));
                        voltageView.setText(String.format("%.2f",voltage)+"V");    // update text views

                        batVoltage = Double.parseDouble(inputJson.getString("batVoltage"));
                        batVoltageView.setText(String.format("%.2f",batVoltage)+"V");

                        current = Math.abs(Double.parseDouble(inputJson.getString("current")));
                        currentView.setText((String.format("%.2f", current) + "A"));

                        series.appendData(new DataPoint(graphLastXValue, voltage), true, 40);
                        graphLastXValue+=1d;

                        series2.appendData(new DataPoint(graph2LastXValue,current), true, 40);
                        graph2LastXValue+=1d;

                        power = current*voltage;
                        series3.appendData(new DataPoint(graph3LastXValue,power), true, 40);
                        graph3LastXValue+=1d;

                        powerView.setText((String.format("%.2f",power) + "W"));

                    } catch (JSONException e) {                                 // catch errors with JSON extraction
                        e.printStackTrace();
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupBt();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getActivity(), R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
        }
    }

    /**
     * Establish connection with other divice
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_solenergi, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.secure_connect_scan: {
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(getActivity(), DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
            }

        }
        return false;
    }
}
