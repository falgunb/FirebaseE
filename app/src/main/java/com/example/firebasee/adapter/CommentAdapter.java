package com.example.firebasee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasee.R;
import com.example.firebasee.model.Comment;
import com.example.firebasee.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    public Context mContext;
    private List<Comment> mComments;

    public CommentAdapter(Context mContext, List<Comment> mComments) {
        this.mContext = mContext;
        this.mComments = mComments;
    }

    private FirebaseUser fUser;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Comment comment = mComments.get(position);
        holder.comment.setText(comment.getComment());
        FirebaseDatabase.getInstance()
                .getReference().child("Users").child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CircleImageView circleImageView;
        public TextView username;
        public TextView comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.image_profile_co);
            username = itemView.findViewById(R.id.comment_item_username);
            comment = itemView.findViewById(R.id.comment_item_comment);
        }
    }
}
