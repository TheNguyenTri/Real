package android.trithe.real.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.trithe.real.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {
    private ImageView newPostImage;
    private EditText textNamePust;
    private Button btnPust;
    private Uri postImageUri = null;
    private ProgressBar newPostProgress;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;
    private Bitmap compressedImageFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        initFirebase();
        initView();
        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImage();
            }
        });
        btnPust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postBlog();
            }
        });
    }

    private void cropImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(NewPostActivity.this);
    }

    private void initFirebase() {
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
    }

    private void postBlog() {
        final String desc = textNamePust.getText().toString();

        if (!TextUtils.isEmpty(desc) && postImageUri != null) {

            newPostProgress.setVisibility(View.VISIBLE);

            final String randomName = UUID.randomUUID().toString();

            // PHOTO UPLOAD
            File newImageFile = new File(postImageUri.getPath());
            try {

                compressedImageFile = new Compressor(NewPostActivity.this)
                        .setMaxHeight(720)
                        .setMaxWidth(720)
                        .setQuality(50)
                        .compressToBitmap(newImageFile);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageData = baos.toByteArray();

            // PHOTO UPLOAD
            UploadTask filePath = storageReference.child("post_images").child(randomName + ".jpg").putBytes(imageData);
            filePath.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                    final String downloadUri = task.getResult().getDownloadUrl().toString();
                    if (task.isSuccessful()) {
                        File newThumbFile = new File(postImageUri.getPath());
                        try {
                            compressedImageFile = new Compressor(NewPostActivity.this)
                                    .setMaxHeight(100)
                                    .setMaxWidth(100)
                                    .setQuality(1)
                                    .compressToBitmap(newThumbFile);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] thumbData = baos.toByteArray();
                        UploadTask uploadTask = storageReference.child("post_images/thumbs")
                                .child(randomName + ".jpg").putBytes(thumbData);
                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();
                                Map<String, Object> postMap = new HashMap<>();
                                postMap.put("image_url", downloadUri);
                                postMap.put("image_thumb", downloadthumbUri);
                                postMap.put("desc", desc);
                                postMap.put("user_id", current_user_id);
                                postMap.put("timestamp", FieldValue.serverTimestamp());
                                firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                            Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                            startActivity(mainIntent);
                                            finish();
                                        }
                                        newPostProgress.setVisibility(View.INVISIBLE);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                    } else {
                        newPostProgress.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }


    private void initView() {
        newPostProgress = findViewById(R.id.new_post_progress);
        newPostImage = findViewById(R.id.new_post_image);
        textNamePust = findViewById(R.id.textNamePust);
        btnPust = findViewById(R.id.btnPust);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void backposst(View view) {
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
