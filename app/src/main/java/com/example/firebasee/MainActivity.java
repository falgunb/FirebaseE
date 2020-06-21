package com.example.firebasee;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button mLogout;
    private ProgressDialog pd;
//    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        mLogout = findViewById(R.id.maLogout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("logging you out..");
                pd.show();
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Successfully Logged out", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                startActivity(new Intent(MainActivity.this, StartActivity.class));
                finish();
            }
        });
    }
}