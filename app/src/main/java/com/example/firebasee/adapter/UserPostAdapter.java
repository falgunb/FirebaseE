package com.example.firebasee.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasee.R;
import com.example.firebasee.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder>{

    private Context mContext;
    private List<Post> userPosts;
    private FirebaseUser firebaseUser;

    public UserPostAdapter(Context context, List<Post> myPostList) {
        this.mContext = context;
        this.userPosts = myPostList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_post_item,parent,false);
        return new UserPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = userPosts.get(position);
        holder.userDesc.setText(post.getDescription());
//        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new Va)

        isLiked(post.getPostId(), holder.userLikeIcon);
        noOfLikes(post.getPostId(), holder.userLikesCount);
        getComments(post.getPostId(), holder.userCommentCount);
        isRetweeted(post.getPostId(), holder.userRetweetCount);
        ifRetweeted(post.getPostId(), holder.userRetweetIcon);

    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userDesc;
        public TextView userLikesCount;
        public TextView userRetweetCount;
        public TextView userCommentCount;

        public ImageView userRetweetIcon;
        public ImageView userLikeIcon;
        public ImageView userCommentIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userDesc = itemView.findViewById(R.id.user_post_description);
            userLikesCount = itemView.findViewById(R.id.user_post_likes_count);
            userRetweetCount = itemView.findViewById(R.id.user_count_of_retweets);
            userCommentCount = itemView.findViewById(R.id.user_post_comment_count);

            userRetweetIcon = itemView.findViewById(R.id.user_post_retweet);
            userLikeIcon = itemView.findViewById(R.id.user_post_likes);
            userCommentIcon = itemView.findViewById(R.id.user_post_comment);
        }
    }

    private void isLiked(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_fav1);
                    imageView.setTag("Liked");
                } else {
                    imageView.setImageResource(R.drawable.ic_fav);
                    imageView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ifRetweeted(String postId, final ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Retweeted").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.retweet);
                    imageView.setTag("Retweeted");
                } else {
                    imageView.setImageResource(R.drawable.ic_tweet1);
                    imageView.setTag("Retweet");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void noOfLikes(String postId, final TextView text) {

        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(dataSnapshot.getChildrenCount() +"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getComments(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isRetweeted(String postId, final TextView text) {
        FirebaseDatabase.getInstance().getReference().child("Retweeted").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                text.setText(dataSnapshot.getChildrenCount() + "");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
