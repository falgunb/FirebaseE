package com.example.firebasee;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.List;

public class AddOnePost extends AppCompatActivity {

    private Uri imageUri;
    private ImageView close, imageAdded;
    SocialAutoCompleteTextView autoCompleteTextView;
    //    private TextView post;
    private Button post1;
    private Button imagePost;
    private String imageUrl;
    private static final String TAG = AddOnePost.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_one_post);
        autoCompleteTextView = findViewById(R.id.description);
        close = findViewById(R.id.close);
//        post = findViewById(R.id.post);
        post1 = findViewById(R.id.post);
        imagePost = findViewById(R.id.postImage);
        imageAdded = findViewById(R.id.image_added);
        imageAdded.setVisibility(View.INVISIBLE);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddOnePost.this, MainActivity.class));
                finish();
            }
        });

        post1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Upload();
            }
        });
        imagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(AddOnePost.this);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
                } else {
                    startCrop(imageUri);
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            imageAdded.setImageURI(result.getUri());
            imageAdded.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Set Image Successful", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    private void Upload() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("uploading, please wait...");
        pd.show();

        if (imageUri != null) {
            final StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis() + "." + getFileExtension(imageUri));
            StorageTask task = filePath.putFile(imageUri);
            Log.d(TAG, "upload called!"+imageUri);
            task.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();
                    Log.d(TAG,"here is image url : "+imageUrl);
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    String postId = ref.push().getKey();

                    HashMap<String, Object> map = new HashMap<>();

                    map.put("postId", postId);
                    map.put("imageurl", imageUrl);
                    map.put("description", autoCompleteTextView.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    ref.child(postId).setValue(map);
                    Log.d(TAG, "onCompleteMapValues: " +map);
//                    DatabaseReference mHashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
//                    List<String> hashTags = autoCompleteTextView.getHashtags();
//                    if (!hashTags.isEmpty()) {
//                        for (String tag : hashTags) {
//                            map.clear();
//
//                            map.put("tag", tag.toLowerCase());
//                            map.put("postId", postId);
//
//                            mHashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
//                        }
//                    }
                    pd.dismiss();
                    Toast.makeText(AddOnePost.this, "Image uploaded successfully. ", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddOnePost.this, MainActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddOnePost.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No image was selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri mUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(mUri));
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