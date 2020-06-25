package com.example.firebasee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebasee.adapter.UserPostAdapter;
import com.example.firebasee.model.Post;
import com.example.firebasee.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserAccountFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserPostAdapter userPostAdapter;
    private List<Post> myPostList;
    private CircleImageView cir_profile;
    private TextView fullName;
    private TextView userName;
    private TextView follower;
    private TextView following;
    private ImageView btnLogout;

    FirebaseUser fUser;
    String profileId;

    public UserAccountFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        profileId = fUser.getUid();

        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString(profileId, "none");
        if (data.equals("none")){
            profileId = fUser.getUid();
        }else {
            profileId = data;
        }

        btnLogout = view.findViewById(R.id.user_logout_button);
        cir_profile = view.findViewById(R.id.user_profile_imageView);
        fullName = view.findViewById(R.id.tv_fullname_user_profile);
        userName = view.findViewById(R.id.tv_username_user_profile);
        follower = view.findViewById(R.id.tv_user_follower_count);
        following = view.findViewById(R.id.tv_user_following_count);
        recyclerView = view.findViewById(R.id.recycler_view_user_posts);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);

        myPostList = new ArrayList<>();
        userPostAdapter = new UserPostAdapter(getContext(),myPostList);
        recyclerView.setAdapter(userPostAdapter);


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setMessage("Logout?");
                alertDialogBuilder.setCancelable(true);

                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("MSG", "onClick: YES");
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getContext(), StartActivity.class));
                        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        Toast.makeText(getContext(), "So, You've Chosen Death?", Toast.LENGTH_SHORT).show();
                        Log.d("MSG", "onClick: No");
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        userInfo();
        getFollowersAndFollowingsCount();
        getPostCount();
        getMyPosts();

        return view;
    }

    private void getMyPosts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPostList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId)){
                        myPostList.add(post);
                    }
                }
                Collections.reverse(myPostList);
                userPostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    if (post.getPublisher().equals(profileId))
                        counter++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingsCount() {
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        mRef.child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                follower.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mRef.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                fullName.setText(user.getName());
                userName.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}