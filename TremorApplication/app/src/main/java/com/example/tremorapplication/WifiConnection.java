package com.example.tremorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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

import java.util.Calendar;

public class WifiConnection extends AppCompatActivity {
    //firebase connection
    FirebaseDatabase database;
    DatabaseReference myRef;
    PatientWI patientw;
    EditText editText,fate,tremoratee;
    TextView tvDatee;
    Button savbutton;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;


    private static final String TAG = "WifiConnection";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_connection);

        editText = (EditText)findViewById(R.id.editText);
        tvDatee=(TextView) findViewById(R.id.tvDatee);
        fate=(EditText) findViewById(R.id.fate);
        tremoratee=(EditText) findViewById(R.id.tremoratee);
        savbutton=(Button) findViewById(R.id.savbutton);



        Button startbtn=(Button)findViewById(R.id.startbtn);
        savbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getValues();
                myRef.child("Progress1").setValue(patientw);

                Toast.makeText(WifiConnection.this,"Data Inserted...",Toast.LENGTH_LONG).show();
                System.out.print("forth thing");

            }
        });

        //DB ACCESS
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("Tremor/Patient");



        ////date picker

        mDisplayDate = (TextView) findViewById(R.id.tvDatee);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        WifiConnection.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };

    }

    private PatientWI getValues(){
        patientw = new PatientWI();
        patientw.setPatientNo(editText.getText().toString());
        patientw.setWeekNo(tvDatee.getText().toString());
        patientw.setFrequencyRate(fate.getText().toString());
        patientw.setTremorRate(tremoratee.getText().toString());
        return patientw;
    }
    public  void savbutton (View view){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                getValues();
                myRef.child("Progress1").setValue(patientw);
                Toast.makeText(WifiConnection.this,"Data Inserted...",Toast.LENGTH_LONG).show();
                System.out.print("forth thing");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
                System.out.print("fifth thing");
            }
        });
    }
    }

