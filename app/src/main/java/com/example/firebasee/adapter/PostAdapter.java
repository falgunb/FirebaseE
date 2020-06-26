package com.example.firebasee.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebasee.CommentActivity;
import com.example.firebasee.R;
import com.example.firebasee.model.Post;
import com.example.firebasee.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    private static final String TAG = PostAdapter.class.getSimpleName();
    private FirebaseUser firebaseUser;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, parent, false);
        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final Post post = mPosts.get(position);

        holder.description.setText(post.getDescription());
        Picasso.get().load(post.getImageUrl()).into(holder.postedImage);
        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                holder.username.setText(user.getUsername());
//                holder.author.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        isLiked(post.getPostId(), holder.like);
        noOfLikes(post.getPostId(), holder.likes);
        getComments(post.getPostId(), holder.noOfComments);
        isRetweeted(post.getPostId(), holder.noOfRetweets);
        ifRetweeted(post.getPostId(), holder.retweet);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.like.getTag().equals("Like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid())
                            .setValue(true);
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid())
                            .removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, CommentActivity.class);
                i.putExtra("postId", post.getPostId());
                i.putExtra("autherId", post.getPublisher());
                mContext.startActivity(i);
            }
        });

        holder.noOfComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, CommentActivity.class);
                i.putExtra("postId", post.getPostId());
                i.putExtra("autherId", post.getPublisher());
                mContext.startActivity(i);
            }
        });
        holder.retweet.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClicked retweet......");
                try {
                    if (holder.retweet.getTag().equals("Retweet")) {
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

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        //SocialAutoCompleteTextView descriptionView;
        public ImageView like;
        public ImageView retweet;
        public ImageView comment;
        public TextView description;
        public TextView username;
        public TextView likes;
        //        public TextView author;
        public TextView noOfComments;
        public TextView noOfRetweets;
        public ImageView post_option_dot;
        public ImageView postedImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //descriptionView = itemView.findViewById(R.id.postedDescription);
            description = itemView.findViewById(R.id.description);
            like = itemView.findViewById(R.id.like);
            retweet = itemView.findViewById(R.id.retweet);
            noOfRetweets = itemView.findViewById(R.id.no_of_retweets);
            comment = itemView.findViewById(R.id.comment);
            username = itemView.findViewById(R.id.post_username);
            likes = itemView.findViewById(R.id.no_of_likes);
            postedImage = itemView.findViewById(R.id.posted_image);
            //author = itemView.findViewById(R.id.author);
            noOfComments = itemView.findViewById(R.id.no_of_comments);
            post_option_dot = itemView.findViewById(R.id.image_option_dot);

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
