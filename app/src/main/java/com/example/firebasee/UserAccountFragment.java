package com.example.firebasee;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;


public class UserAccountFragment extends Fragment {

    private Button btnLogout;

    public UserAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);
        btnLogout = view.findViewById(R.id.user_logout_button);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), StartActivity.class));
                Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}