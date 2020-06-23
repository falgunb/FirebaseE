package com.example.firebasee;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;

public class AddOnePost extends AppCompatActivity {

    private Uri imageUri;
    private ImageView close, imageAdded;
    SocialAutoCompleteTextView autoCompleteTextView;
    private TextView post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_one_post);
        autoCompleteTextView = findViewById(R.id.description);
        close = findViewById(R.id.close);
        post = findViewById(R.id.post);
//        imageAdded = findViewById(R.id.image_added);
//        imageAdded.setVisibility(View.VISIBLE);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddOnePost.this, MainActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Upload();
            }
        });

//        CropImage.activity().start(AddOnePost.this);
    }

    private void Upload() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        String postId = ref.push().getKey();

        HashMap<String , Object> map = new HashMap<>();
        map.put("postId" , postId);
        map.put("description" , autoCompleteTextView.getText().toString());
        map.put("publisher" , FirebaseAuth.getInstance().getCurrentUser().getUid());

        ref.child(postId).setValue(map);

        DatabaseReference mHashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
        List<String> hashTags = autoCompleteTextView.getHashtags();
        if (!hashTags.isEmpty()){
            for (String tag : hashTags){
                map.clear();

                map.put("tag" , tag.toLowerCase());
                map.put("postId" , postId);

                mHashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
            }
        }
        startActivity(new Intent(AddOnePost.this , MainActivity.class));
        finish();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            imageUri = result.getUri();
//            imageAdded.setImageURI(imageUri);
//        } else {
//            Toast.makeText(this, "Try again..", Toast.LENGTH_SHORT).show();
//        }
//    }
}