package com.example.controller;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String macAddress = "98:D3:31:FD:2F:B1";

    private SmoothBluetooth mSmoothBluetooth;

    private Button conBtn;
    private Switch connected;

    private boolean CRLF = false;

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSmoothBluetooth =  new SmoothBluetooth(getBaseContext(), mListener);

        connected = (Switch) findViewById(R.id.connected);

        conBtn = (Button) findViewById(R.id.con);
        conBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mSmoothBluetooth.tryConnection();
            }
        });

        Button up = (Button)findViewById(R.id.UP);
        Button down = (Button)findViewById(R.id.DOWN);
        Button left = (Button)findViewById(R.id.LEFT);
        Button right = (Button)findViewById(R.id.RIGHT);
        up.setOnTouchListener(new RepeatListener(400, 100, this));
        down.setOnTouchListener(new RepeatListener(400, 100, this));
        left.setOnTouchListener(new RepeatListener(400, 100, this));
        right.setOnTouchListener(new RepeatListener(400, 100, this));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSmoothBluetooth.stop();
    }

    private SmoothBluetooth.Listener mListener = new SmoothBluetooth.Listener() {
        @Override
        public void onBluetoothNotSupported() {
            //device does not support bluetooth
        }

        @Override
        public void onBluetoothNotEnabled() {
            //bluetooth is disabled, probably call Intent request to enable bluetooth
            Toast toast = Toast.makeText(getApplicationContext(), "Bluetooth is disabled :(", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onConnecting(Device device) {
            //called when connecting to particular device
            Toast toast = Toast.makeText(getApplicationContext(), "Connecting to " + device.getName(), Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onConnected(Device device) {
            //called when connected to particular device
            System.out.println("Connected to " + device.getName() + ", " + device.getAddress());

            connected.setChecked(true);

            Toast toast = Toast.makeText(getApplicationContext(), "Connected to " + device.getName(), Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onDisconnected() {
            //called when disconnected from device
            System.out.println("Connection lost!");
            connected.setChecked(false);

            Toast toast = Toast.makeText(getApplicationContext(), "Connection lost!", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onConnectionFailed(Device device) {
            //called when connection failed to particular device
            System.out.println("Connection failed!");
            connected.setChecked(false);

            Toast toast = Toast.makeText(getApplicationContext(), "Connection failed!", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onDiscoveryStarted() {
            //called when discovery is started
            System.out.println("søger efter devices");

            Toast toast = Toast.makeText(getApplicationContext(), "Søger efter RC car!", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onDiscoveryFinished() {
            //called when discovery is finished
            System.out.println("søgning færdig!");

            Toast toast = Toast.makeText(getApplicationContext(), "Søgning færdig :)", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onNoDevicesFound() {
            //called when no devices found

            Toast toast = Toast.makeText(getApplicationContext(), "Fandt ikke RC CAR???", Toast.LENGTH_SHORT);
            toast.show();
        }

        @Override
        public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {
            for (Device device: deviceList) {
                System.out.println(device.getAddress());
                if(device.getAddress().equals(macAddress)){
                    System.out.println("tyring to connect to " + device.getName());
                    connectionCallback.connectTo(device);
                }
            }
        }

        @Override
        public void onDataReceived(int data) {
            //receives all bytes
        }
    };

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.UP:
                mSmoothBluetooth.send("U",CRLF);
                break;
            case R.id.DOWN:
                mSmoothBluetooth.send("D",CRLF);
                break;
            case R.id.LEFT:
                mSmoothBluetooth.send("L",CRLF);
                break;
            case R.id.RIGHT:
                mSmoothBluetooth.send("R",CRLF);
                break;
        }
    }
}
