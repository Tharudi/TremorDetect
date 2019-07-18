package com.example.tremorapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.support.v7.app.AppCompatActivity;import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
//import android.support.v7.app.AlertDialog;

public class tremor extends AppCompatActivity implements SensorEventListener  {

    private static final String TAG = "activity_tremor";

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


   private SensorManager sensorManager;
   Sensor accelerometer ;
    TextView acceleration;
    private Button startbtn;
    //firebase connection
    FirebaseDatabase database;
    DatabaseReference myRef;
    Patient patient;
    User user;
    EditText editText1,frate,tremorratee;
    TextView tvDate;
    Button savebutton;

    Calendar date;
   private SensorEventListener SensorListener;
   private float 𝑠𝑋;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tremor);

        date = Calendar.getInstance();

        editText1 = (EditText)findViewById(R.id.editText1);
        tvDate=(TextView) findViewById(R.id.tvDate);
        frate=(EditText) findViewById(R.id.frate);
        tremorratee=(EditText) findViewById(R.id.tremorratee);
        savebutton=(Button) findViewById(R.id.savebutton);
        Button startbtn=(Button)findViewById(R.id.startbtn);

        savebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Patient p = getValues();

                int lastId =0;

                DatabaseReference child = myRef.child(LogIn.loggedUser.getId());

                lastId = LogIn.loggedUser.getChildCount();

                myRef.child(LogIn.loggedUser.getId()).child("test"+ ++lastId).setValue(patient);

                Toast.makeText(tremor.this,"Data Inserted...",Toast.LENGTH_LONG).show();

                System.out.print("forth thing");

            }
        });
       startbtn.setOnClickListener(new View.OnClickListener() {
           @RequiresApi(api = Build.VERSION_CODES.KITKAT)
         @Override
            public void onClick(View v) {
             Log.d(TAG,"onCreate:Initializing Sensor Servive");
               Log.d(TAG, "onCreate:Initializing Sensor Service");
               sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
               accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
              sensorManager.registerListener((SensorEventListener) tremor.this, accelerometer,200000000, sensorManager.SENSOR_DELAY_NORMAL );


               acceleration = (TextView) findViewById(R.id.acceleration);
                Log.d(TAG, "onCreate: Register Accelerometer");

//                //alert
              AlertDialog.Builder builder=new AlertDialog.Builder(tremor.this);
               builder.setCancelable(true);
                builder.setTitle("This is an Alert");
                builder.setMessage("Your Hand Tremor was detected");

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.cancel();
                   }
               });
              builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                        acceleration.setVisibility(View.VISIBLE);
                  }
              });
               builder.show();
                Button savebutton=(Button)findViewById(R.id.savebutton);
                savebutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

            }
       });

        //DB ACCESS
        database = FirebaseDatabase.getInstance();
       myRef = database.getReference("User");
        //final DatabaseReference table_user=database.getReference("User");



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
                        tremor.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                // set current date into datepicker
               // mDisplayDate.init(year, month, day, null);
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + ++month + "/" + day + "/" + year);

                date.set(Calendar.MONTH, month);
                date.set(Calendar.YEAR, year);
                date.set(Calendar.DAY_OF_MONTH, day);
                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };

    }

    private  Patient getValues(){
        patient = new Patient();

        patient.setPatientNo(editText1.getText().toString());
//        if(date!= null ) patient.setDate(date.getTimeInMillis());
      //  patient.setFrequencyRate(frate.getText().toString());
        patient.setTremorRate(tremorratee.getText().toString());
       // patient.setPatientNo(editText1.setText(user.setName()));

        return patient;

    }
   public User setValues(){
       user=new User();
       user.setName(editText1.getText().toString());
       return user;
   }



    public  void savebutton (View view){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getValues();
                setValues();
                myRef.child("User/name").setValue(patient);
                Toast.makeText(tremor.this,"Data Inserted...",Toast.LENGTH_LONG).show();
                System.out.print("forth thing");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                System.out.print("fifth thing");
            }
        });
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        acceleration.setText("\n ax:" + event.values[0] + "\n ay: " + event.values[1] + "\n az:" + event.values[2]);
        Log.d(TAG, "onSensorChanged: ax: " + event.values[0] + "ay: " + event.values[1] + "az:" + event.values[2]);
        //Log.d()

        //frequency calculation
        int Ts = 5;
        float 𝑠𝑋𝑜 = 0;
        float 𝑠𝑌𝑜 = 0;
        float 𝑠𝑍𝑜 = 0;
        float 𝑎𝑋 = event.values[0];
        float 𝑎𝑌 = event.values[1];
        float 𝑎𝑍 = event.values[2];
       //float  ax=2;
        final float a = (𝑎𝑋 + 𝑎𝑌 + 𝑎𝑍) / 3;
       // final float sz = 𝑠𝑍𝑜 + ((1 / 2) * 𝑎𝑍 * Ts * Ts);
       // final float sx = 𝑠𝑋𝑜+((1 / 2) * ax * Ts * Ts);
       // final float sy = 𝑠𝑌𝑜 + ((1 / 2) * 𝑎𝑌 * Ts * Ts);
        //mean displacement
       float kx= (float) (0.5*25);
        float sX=kx*𝑎𝑋;
        float sY=kx*𝑎𝑌;
        float sZ=kx*𝑎𝑍;
        final float X = (sX + sY +sZ)/ 3;
        //find frequency time
        float pi=7*7/22*22;
       /// float f;
       // a=(2*pi*f)*2*pi*f)*s;
        float j=a/X;
        float y=j/4;
       // float q=pi*f
        float f=y/pi;
//        final float f = (float) Math.sqrt(a / (X * 4 * pi));
//        //float f;
      //  float pi = 22 / 7;
//        float f= (float) Math.sqrt(a/(s*4*dpi));
//        //get amplitude
        int u = 0;
//
        float pie=7/22;
        float m=2*pie*f;
       // float f = (float) (s /( a * Math.cos(2*pi)));
        float l= (float) (X/Math.cos(m));
        float lamda=l/10;
      frate.setText("F:" +a);
       // float v = u + a * Ts;
        //A=Distance/frequency;
       // float lamda = X / f;



        if ((lamda == 0 )|| (𝑎𝑋<1)) {
            tremorratee.setText("0 :No Tremor");
        }
        if (lamda < 1 && lamda > 0.5) {
            tremorratee.setText("1 :Slight Tremor");
        }
        if (lamda < 3 && lamda > 1) {
            tremorratee.setText("2 :Mild Tremor");
        }
//        if (lamda < 10 && lamda >= 3) {
//            tremorratee.setText("3 :Moderate Tremor");
//        }
        if (lamda >= 10) {
            tremorratee.setText("4 :Severe Tremor");
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this code will be executed after 10 seconds

              //  tremorratee.setText("abc"+ a);
            }
        }, 10000);
        /*
*/
    }




}
