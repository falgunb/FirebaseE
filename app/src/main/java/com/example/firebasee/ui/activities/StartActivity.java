package com.example.firebasee.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.firebasee.R;
import com.google.firebase.auth.FirebaseAuth;

public class StartActivity extends AppCompatActivity {

    private ImageView image;
    private Button logIn, signUp;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        image = findViewById(R.id.accountBox);
        logIn = findViewById(R.id.login);
        signUp = findViewById(R.id.signup);
        linearLayout = findViewById(R.id.linearLayout);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        linearLayout.animate().alpha(0f).setDuration(1);
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, -1000);
        animation.setDuration(1000);
        animation.setFillAfter(false);
        animation.setAnimationListener(new MyAnimationListner());
        image.setAnimation(animation);
    }

    private class MyAnimationListner implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            image.clearAnimation();
            image.setVisibility(View.INVISIBLE);
            linearLayout.animate().alpha(1f).setDuration(1000);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            finish();
        }
    }
}