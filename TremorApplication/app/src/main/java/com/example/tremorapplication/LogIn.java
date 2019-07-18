package com.example.tremorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LogIn extends AppCompatActivity {
    Button btnSignIn, btnSignUp;
    EditText phonenoe, passworde;

    public static User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        passworde = findViewById(R.id.passworde);
        phonenoe = findViewById(R.id.phonenoe);

        //Initialize database

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LogIn.this,
                        SignUp.class);
                startActivity(myIntent);
            }
        });

        Button btnSignIn = (Button) findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog mDialog = new ProgressDialog(LogIn.this);
                mDialog.setMessage("Please waiting...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // get user implementation
                        mDialog.dismiss();
                        //check if user is not exist in database

                        String phone = phonenoe.getText().toString();

                        if (dataSnapshot.child(phone).exists()) {
                            User user = dataSnapshot.child(phone).getValue(User.class);
                            if (user.getPassword().equals(passworde.getText().toString())) {
                                Toast.makeText(LogIn.this, "Sign in sucessfully", Toast.LENGTH_SHORT).show();

                                loggedUser = user;

                                Intent myIntent = new Intent(LogIn.this,
                                        MainActivity.class);
                                startActivity(myIntent);

                            } else {
                                Toast.makeText(LogIn.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            mDialog.dismiss();
                            Toast.makeText(LogIn.this, "User is not exists", Toast.LENGTH_SHORT).show();
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
