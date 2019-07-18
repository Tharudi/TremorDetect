package com.example.tremorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class patientHistory extends AppCompatActivity {

    FirebaseDatabase database;
    private DatabaseReference myRef;
    private EditText wk1;
    private EditText wk2;
    private EditText wk3;
    private EditText wk4;
    private EditText wk5;

    private EditText monthText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history);

        database= FirebaseDatabase.getInstance();
        myRef=database.getReference("Tremor/Patient/Progress");

         wk1 = (EditText) findViewById(R.id.wk1e);
        wk2 = (EditText) findViewById(R.id.wk2e);
        wk3 = (EditText) findViewById(R.id.wk3e);
        wk4 = (EditText) findViewById(R.id.wk4e);
        wk5 = (EditText) findViewById(R.id.wk5e);
         monthText = (EditText) findViewById(R.id.editText2);

         monthText.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }

             @Override
             public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

             }

             @Override
             public void afterTextChanged(Editable editable) {
                String month = editable.toString();
                if(month != null)
                try{
                    final int monthI = Integer.parseInt(month);
                    if(monthI<12){
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                                for (DataSnapshot s :
                                        children) {
                                    Patient pat = s.getValue(Patient.class);
                                    Calendar date = Calendar.getInstance();
                                    date.setTimeInMillis(pat.getDate());

                                    if (date != null ) Log.d("DATE: ",  date.get(Calendar.MONTH) +" / "+ date.get(Calendar.WEEK_OF_MONTH)+"");

                                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==1 && date.get(Calendar.MONTH)== monthI-1){
                                        wk1.setText(pat.getTremorRate());
                                    }
                                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==2 && date.get(Calendar.MONTH)== monthI-1){
                                        wk2.setText(pat.getTremorRate());
                                    }
                                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==3 && date.get(Calendar.MONTH)== monthI-1){
                                        wk3.setText(pat.getTremorRate());
                                    }
                                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==4 && date.get(Calendar.MONTH)== monthI-1){
                                        wk4.setText(pat.getTremorRate());
                                    }
                                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==5 && date.get(Calendar.MONTH)== monthI-1){
                                        wk5.setText(pat.getTremorRate());
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }catch (Exception e){
                    Log.d("ERROR!...", e.getMessage());
                }
             }
         });



        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot s :
                        children) {
                    Patient pat = s.getValue(Patient.class);
                    Calendar date = Calendar.getInstance();
                    date.setTimeInMillis(pat.getDate());

                    if (date != null ) Log.d("DATE: ", date.get(Calendar.WEEK_OF_MONTH)+"");

                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==1){
                        wk1.setText(pat.getTremorRate());
                    }
                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==2){
                        wk2.setText(pat.getTremorRate());
                    }
                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==3){
                        wk3.setText(pat.getTremorRate());
                    }
                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==4){
                        wk4.setText(pat.getTremorRate());
                    }
                    if(date!= null && date.get(Calendar.WEEK_OF_MONTH )==5){
                        wk5.setText(pat.getTremorRate());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

       }

    }

