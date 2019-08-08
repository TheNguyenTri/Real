package android.trithe.real.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetUpActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog pDialog;
    private CircleImageView image;
    private EditText textName;
    private Button btn;
    private Uri mainImageURI = null;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String user_id;
    private boolean isChanged = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        initView();
        initFirebase();
        image.setOnClickListener(this);
        btn.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image:
                BringImagePicker();
                break;
            case R.id.btn:
                setUp();
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setUp() {
        final String username = textName.getText().toString();
        if (!username.equals("") && mainImageURI != null) {
            pDialog = new ProgressDialog(SetUpActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
            if (isChanged) {
                user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
                StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                image_path.putFile(mainImageURI).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        storeFireBase(task, username);
                    } else {
                        Toast.makeText(getApplicationContext(), "Upload Error", Toast.LENGTH_SHORT).show();
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                    }
                });
            } else {
                storeFireBase(null, username);
            }
        }
    }

    private void initView() {
        image = findViewById(R.id.image);
        textName = findViewById(R.id.textName);
        btn = findViewById(R.id.btn);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(SetUpActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = Objects.requireNonNull(result).getUri();
                image.setImageURI(mainImageURI);
                isChanged = true;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void storeFireBase(@NonNull Task<UploadTask.TaskSnapshot> task, final String username) {
        final Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getDownloadUrl();
        } else {
            download_uri = mainImageURI;
        }
        String token_id = FirebaseInstanceId.getInstance().getToken();
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("status", "Mới tới");
        userMap.put("image", String.valueOf(download_uri));
        userMap.put("token_id", Objects.requireNonNull(token_id));
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()) {
                Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                Toast.makeText(getApplicationContext(), Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (pDialog.isShowing())
                pDialog.dismiss();
        });
    }
}
