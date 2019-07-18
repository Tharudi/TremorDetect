package com.example.tremorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    EditText phonenoe,usernamee,passworde;
    Button btnSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernamee=findViewById(R.id.usernamee);
        passworde=findViewById(R.id.passworde);
        phonenoe=findViewById(R.id.phonenoe);

        //Initialize database

        FirebaseDatabase database=FirebaseDatabase.getInstance();
        final DatabaseReference table_user=database.getReference("User");

        Button btnSignUp=(Button)findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog mDialog=new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //check whether already user
                        if(dataSnapshot.child(phonenoe.getText().toString()).exists()) {
                            mDialog.dismiss();

                            Toast.makeText(SignUp.this,"Phone number is already exists",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            mDialog.dismiss();
                           // Toast.makeText(SignUp.this,"Phone number is already exists",Toast.LENGTH_SHORT).show();
                            User user=new User(usernamee.getText().toString(),passworde.getText().toString());
                            user.setId(phonenoe.getText().toString());
                            user.setChildCount(0);
                            table_user.child(phonenoe.getText().toString()).setValue(user);
                            Toast.makeText(SignUp.this,"Sign up Sucessfully",Toast.LENGTH_SHORT).show();
//                            Intent myIntent = new Intent(SignUp.this,
//                                    MainActivity.class);
//                            startActivity(myIntent);
                            finish();

                        }
                        }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

}
