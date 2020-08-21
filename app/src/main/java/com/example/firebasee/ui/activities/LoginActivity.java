package com.example.firebasee.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasee.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText laEmail, laPassword;
    private Button laSignIn;
    private TextView regUser;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pd = new ProgressDialog(this);

        laEmail = findViewById(R.id.liEmail);
        laPassword = findViewById(R.id.liPassword);
        laSignIn = findViewById(R.id.liLogin);
        regUser = findViewById(R.id.regUser);

        mAuth = FirebaseAuth.getInstance();

        regUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
            }
        });

        laSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Logging in...");
                pd.show();
                String txt_email= laEmail.getText().toString();
                String txt_password = laPassword.getText().toString();
                if(TextUtils.isEmpty(txt_email)|| TextUtils.isEmpty(txt_password)){
                    Toast.makeText(LoginActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }else {
                    loginUser(txt_email,txt_password);
                }
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Toast.makeText(LoginActivity.this, "Update the profile " +
                            "for better experience", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(LoginActivity.this, "Unable to login" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}