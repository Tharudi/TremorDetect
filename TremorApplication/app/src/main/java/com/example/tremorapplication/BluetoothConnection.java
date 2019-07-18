package com.example.tremorapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;


public class BluetoothConnection extends AppCompatActivity {

    private static final String TAG = "BluetoothConnection";
    private final double Ts = 0.1;
    //AT+ADDR="00:21:13:05:B4:E4"
    private final String DEVICE_NAME = "=BTM";
    private final String DEVICE_ADDRESS = "00:21:13:05:B7:8D";
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    FirebaseDatabase database;
    DatabaseReference myRef;
    Patient patient;
    User user;
    EditText name, TremorEditText;
    TextView tvDate;
    RadioButton radioright;
    RadioButton radioleft;
    RadioGroup radioHand;
    Button savebutton;
    Calendar date;
    //EditText editText;
    boolean deviceConnected = false;
    Thread thread;
    byte buffer[];
    int bufferPosition;
    boolean stopThread;
    String stream = "";
    //Button startButton, sendButton,clearButton,stopButton;
    private Button start, close;
    private TextView textView;
    private double finalValue;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    //private double finalValue;
    private Map<String, Double> xyz = new HashMap<>();
    private  GraphView graph;
    private int x= 0;
    LineGraphSeries <DataPoint> series;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connection);


        graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.VISIBLE);

        try {

            series = new LineGraphSeries< >(new DataPoint[] {
                    new DataPoint(0, 0)
            });

            graph.addSeries(series);
        } catch (IllegalArgumentException e) {
            Toast.makeText(BluetoothConnection.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        start = findViewById(R.id.btn_start);
        close = findViewById(R.id.btn_close);
        textView = findViewById(R.id.textView);

        name = (EditText) findViewById(R.id.name);
        tvDate = (TextView) findViewById(R.id.tvDate);

        TremorEditText = (EditText) findViewById(R.id.TremorEditText);
        savebutton = (Button) findViewById(R.id.savebutton);

        radioHand = (RadioGroup) findViewById(R.id.radiohand);
        radioright = (RadioButton) findViewById(R.id.radioright);
        radioleft = (RadioButton) findViewById(R.id.radioleft);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("User");

        xyz = new HashMap<>();

        name.setText(LogIn.loggedUser.getName());

        if (BTinit()) {
            if (BTconnect()) {
                deviceConnected = true;
                beginListenForData();
            }
        }

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xyz = new HashMap<>();
                Toast.makeText(getApplicationContext(), "turning on...", Toast.LENGTH_SHORT).show();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
            }
        });

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    connectionClose();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        ////date picker

        mDisplayDate = (TextView) findViewById(R.id.tvDate);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        BluetoothConnection.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                //Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };
    }

    private String selectHand = "Left";

    private void setHand(String hand){
        selectHand = hand;
    }

    private void connectionClose() throws IOException {

        try {
            stopThread = true;

             outputStream.close();
            inputStream.close();
            socket.close();
            deviceConnected = false;
            textView.append("\nConnection Closed!\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        name = (EditText) findViewById(R.id.name);
        tvDate = (TextView) findViewById(R.id.tvDate);
        TremorEditText = (EditText) findViewById(R.id.TremorEditText);
        radioright = (RadioButton) findViewById(R.id.radioright);
        radioleft = (RadioButton) findViewById(R.id.radioleft);
        savebutton = (Button) findViewById(R.id.savebutton);
        //radioHand = (RadioGroup) findViewById(R.id.radihand);

        saveData();



    }

    private Patient getValues() {

        String userId = LogIn.loggedUser.getId();
        date = Calendar.getInstance();



        int checkedRadioButtonId = radioHand.getCheckedRadioButtonId();
        RadioButton selected = (RadioButton) findViewById(checkedRadioButtonId);
        String hand = selected.getText().toString();
        patient = new Patient();

        patient.setPatientNo(userId);
        patient.setHand(hand);
        if (date != null) patient.setDate(date.getTimeInMillis());
        patient.setTremorRate(TremorEditText.getText().toString());
        // patient.setPatientNo(editText1.setText(user.setName()));

        return patient;

    }

    public void saveData() {
        Patient p = getValues();
        int lastId = 0;

        lastId = LogIn.loggedUser.getChildCount();
        Log.d("LAST ID : ", lastId+"");
        myRef.child(LogIn.loggedUser.getId()).child("Record" + ++lastId).setValue(patient);

        LogIn.loggedUser.setChildCount(lastId);
        myRef.child(LogIn.loggedUser.getId()).child("childCount").setValue(lastId);

        Toast.makeText(BluetoothConnection.this, "Data Inserted...", Toast.LENGTH_LONG).show();

    }

    private void stop() {
        double sx = getValue("x") * Ts * Ts / 2;
        double sy = getValue("y") * Ts * Ts / 2;
        double sz = getValue("z") * Ts * Ts / 2;

        double maxValue;
        if (sx > sy) maxValue = sx;
        else maxValue = sy;

        if (maxValue < sz) maxValue = sz;


        finalValue = maxValue * 100;
        textView.append("");
        textView.append("Final Value = " + finalValue);

        String res = "";

        if (finalValue < 0.6) {
            res = "No Tremor";
        } else if (finalValue < 0.7) {
            res = "Slight Tremor";
        } else if (finalValue < 0.8) {
            res = "Mild Tremor";
        } else if (finalValue < 1) {
            res = "Moderate Tremor";
        } else {
            res = "Severe Tremor";
        }


        textView.setText("Amplitude"+finalValue);
        textView.setText("");
        textView.setText(res);
        TremorEditText.setText(res);

    }
    // XYZ Value Handler

    public boolean BTinit() {
        boolean found = false;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesn't Support Bluetooth", Toast.LENGTH_SHORT).show();
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if (bondedDevices.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Pair the Device first", Toast.LENGTH_SHORT).show();
        } else {
            for (BluetoothDevice iterator : bondedDevices) {
                if (iterator.getAddress().equals(DEVICE_ADDRESS)) {
                    device = iterator;
                    found = true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTconnect() {
        boolean connected = true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected = false;
        }
        if (connected) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream = socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }

    private void setValues(String key, double value) {

        key = key.toLowerCase().trim();
        if (!xyz.containsKey(key) || xyz.get(key) < value) {
            xyz.put(key, value);
            if (key == "x"){
                x++;
                series.appendData(new DataPoint(x,value),true,500);
            }
            final String text = "X = " + getValue("x") + ", Y = " + getValue("y") + ", Z = " + getValue("z");
            textView.setText(text);
        }
    }

    private Double getValue(String key) {
        key = key.toLowerCase().trim();
        return xyz.get(key) != null ? xyz.get(key) : 0.0;
    }

    private void concatStream(String data) {
        stream = stream.concat(data);
        //Log.i("STREAM", stream);
        finalizeStream();
    }

    private void finalizeStream() {
        Log.d("Stream - ", stream);
        String[] split = stream.split("\\|");
        int loop;
        if (stream.endsWith("|")) {
            stream = "";
            loop = split.length;
        } else {
            stream = split[split.length - 1];
            loop = split.length - 1;
        }

        for (int i = 0; i < loop; i++) {
            String allV = split[i];
            String[] vs = allV.split("\r\n");
            if (vs.length == 3) {
                for (String val : vs) {
                    String[] fv = val.split("=");
                    double v = 0;
                    try {
                        v = Double.parseDouble(fv[1].trim());
                        if (v < 0) v = v * -1;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setValues(fv[0], v);
                }
            }
        }

    }

    void beginListenForData() {
        //stopThread = false;
        buffer = new byte[1024];
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        int byteCount = inputStream.available();
                        if (byteCount > 0) {
                            byte[] rawBytes = new byte[byteCount];
                            inputStream.read(rawBytes);
                            final String string = new String(rawBytes, "UTF-8");

                            concatStream(string);
                            Log.d("RECV: ",string);

                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }
}
