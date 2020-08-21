package com.example.firebasee.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasee.ui.activities.CommentActivity;
import com.example.firebasee.R;
import com.example.firebasee.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ViewHolder> {

    public static final String TAG = UserPostAdapter.class.getSimpleName();
    private Context mContext;
    private List<Post> userPosts;
    private FirebaseUser firebaseUser;
    String postId;

    public UserPostAdapter(Context context, List<Post> myPostList, String postId) {
        this.mContext = context;
        this.userPosts = myPostList;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.postId = postId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_post_item, parent, false);
        return new UserPostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = userPosts.get(position);

        holder.userDesc.setText(post.getDescription());

        if (holder.userPostedImage == null){
            holder.userPostedImage.setVisibility(View.GONE);
        }else{
            Picasso.get().load(post.getImageUrl()).into(holder.userPostedImage);
            holder.userPostedImage.setVisibility(View.VISIBLE);
        }
        Log.d(TAG,"url is : " + post.getImageUrl());

        isLiked(post.getPostId(), holder.userLikeIcon);
        noOfLikes(post.getPostId(), holder.userLikesCount);
        getComments(post.getPostId(), holder.userCommentCount);
        isRetweeted(post.getPostId(), holder.userRetweetCount);
        ifRetweeted(post.getPostId(), holder.userRetweetIcon);

        holder.userLikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.userLikeIcon.getTag().equals("Like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid())
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });


        holder.userCommentIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, CommentActivity.class);
                i.putExtra("postId", post.getPostId());
                i.putExtra("autherId", post.getPublisher());
                mContext.startActivity(i);
            }
        });

        holder.userCommentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, CommentActivity.class);
                i.putExtra("postId", post.getPostId());
                i.putExtra("autherId", post.getPublisher());
                mContext.startActivity(i);
            }
        });

        holder.userRetweetIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClicked Retweet......");
                try {
                    if (holder.userRetweetIcon.getTag().equals("Retweet")) {
                        FirebaseDatabase.getInstance().getReference().child("Retweeted").child(post.getPostId()).child(firebaseUser.getUid())
                                .setValue(true);
                    } else {
                        FirebaseDatabase.getInstance().getReference().child("Retweeted").child(post.getPostId()).child(firebaseUser.getUid())
                                .removeValue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.post_options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final ProgressDialog pd = new ProgressDialog(mContext);
                pd.setMessage("Please wait...");
                Log.d(TAG, "onClicked post Options......");
                if (post.getPublisher().endsWith(firebaseUser.getUid())) {
                    final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                    alertDialog.setTitle("Do you want to delete this Post? this cant be undone!");
                    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            pd.show();
                            Log.d(TAG, "Alert Dialog yes pressedddd....");
                            try {
                                FirebaseDatabase.getInstance().getReference().child("Posts").child(post.getPostId())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mContext, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                            dialogInterface.dismiss();
                                            pd.dismiss();
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    });

                    alertDialog.show();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return userPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView userDesc;
        public TextView userLikesCount;
        public TextView userRetweetCount;
        public TextView userCommentCount;

        public ImageView userPostedImage;
        public ImageView post_options;
        public ImageView userRetweetIcon;
        public ImageView userLikeIcon;
        public ImageView userCommentIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userDesc = itemView.findViewById(R.id.user_post_description);
            userLikesCount = itemView.findViewById(R.id.user_post_likes_count);
            userRetweetCount = itemView.findViewById(R.id.user_count_of_retweets);
            userCommentCount = itemView.findViewById(R.id.user_post_comment_count);

            userPostedImage = itemView.findViewById(R.id.user_posted_image);
            post_options = itemView.findViewById(R.id.image_option_dot);
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
                text.setText(dataSnapshot.getChildrenCount() + "");
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
