package android.trithe.real.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.trithe.real.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Objects;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog pDialog;
    private ImageView newPostImage;
    private EditText textNamePust;
    private Button btnPust;
    private Uri postImageUri = null;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;
    private Bitmap compressedImageFile;
    private ImageView imgBack;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        initFireBase();
        initView();
        listener();
    }

    private void listener() {
        newPostImage.setOnClickListener(this);
        btnPust.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    private void cropImage() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMinCropResultSize(512, 512)
                .setAspectRatio(1, 1)
                .start(NewPostActivity.this);
    }

    private void showPDialog() {
        pDialog = new ProgressDialog(NewPostActivity.this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void hidePDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFireBase() {
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }

    private void postBlog() {
        final String desc = textNamePust.getText().toString();
        if (!TextUtils.isEmpty(desc) && postImageUri != null) {
            showPDialog();
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
            filePath.addOnCompleteListener(task -> {
                final String downloadUri = Objects.requireNonNull(task.getResult().getDownloadUrl()).toString();
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
                    ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                    compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                    byte[] thumbData = baos1.toByteArray();
                    UploadTask uploadTask = storageReference.child("post_images/thumbs")
                            .child(randomName + ".jpg").putBytes(thumbData);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                        String downloadThumbUri = Objects.requireNonNull(taskSnapshot.getDownloadUrl()).toString();
                        Map<String, Object> postMap = new HashMap<>();
                        postMap.put("image_url", downloadUri);
                        postMap.put("image_thumb", downloadThumbUri);
                        postMap.put("desc", desc);
                        postMap.put("user_id", current_user_id);
                        postMap.put("timestamp", FieldValue.serverTimestamp());
                        firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                hidePDialog();
                                Toast.makeText(NewPostActivity.this, "Post was added", Toast.LENGTH_LONG).show();
                                Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
                                startActivity(mainIntent);
                                finish();
                            }
                        });
                    }).addOnFailureListener(e -> {
                    });
                }
            });
        }
    }

    private void initView() {
        newPostImage = findViewById(R.id.new_post_image);
        textNamePust = findViewById(R.id.textNamePust);
        btnPust = findViewById(R.id.btnPust);
        imgBack = findViewById(R.id.imgBack);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = Objects.requireNonNull(result).getUri();
                newPostImage.setImageURI(postImageUri);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_post_image:
                cropImage();
                break;
            case R.id.btnPust:
                postBlog();
                break;
            case R.id.imgBack:
                onBackPressed();
                break;
        }
    }
}