package android.trithe.real;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.activity.MainActivity;
import android.trithe.real.activity.SetUpActivity;
import android.trithe.real.adapter.LogoutAdapter;
import android.trithe.real.adapter.ProfileAdapter;
import android.trithe.real.helper.AlarmReceiver;
import android.trithe.real.helper.NotificationScheduler;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.Configuration;
import android.trithe.real.model.Logout;
import android.trithe.real.model.Planss;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView imgAvatar;
    private TextView tvUsername;
    private RecyclerView infoRecyclerView, recyclerViewlogout;
    private List<Configuration> listConfig = new ArrayList<>();
    private List<Logout> listLogout = new ArrayList<>();
    private ProfileAdapter profileAdapter;
    private LogoutAdapter logoutAdapter;
    private boolean isChanged = false;
    private FirebaseAuth firebaseAuth;
    private Uri mainImageURI = null;
    private String user_id, email;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private ProgressBar progressBar;
    private String name,status;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        email = firebaseAuth.getCurrentUser().getEmail();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressBar.setVisibility(View.VISIBLE);
            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            name = task.getResult().getString("name");
                            status = task.getResult().getString("status");
                            String image = task.getResult().getString("image");
                            mainImageURI = Uri.parse(image);
                            tvUsername.setText(name);
                                tvStatus.setText(status);
                                if(tvStatus.getText().equals("")){
                                    tvStatus.setText("Hello there , Welcome to Chat App");
                                }
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions.placeholder(R.drawable.profile);
                            Glide.with(ProfileActivity.this).setDefaultRequestOptions(requestOptions).load(image).into(imgAvatar);
                        }else {
                            tvUsername.setText(firebaseAuth.getCurrentUser().getDisplayName());
                            Glide.with(getApplicationContext()).load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(imgAvatar);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });

        listConfig.add(new Configuration("Email", email, R.drawable.ic_email_black_24dp));
        listLogout.add(new Logout("Signout", R.drawable.ic_settings_power_black_24dp));
        profileAdapter = new ProfileAdapter(this, listConfig);
        logoutAdapter = new LogoutAdapter(this, listLogout, new OnClick() {
            @Override
            public void onItemClickClicked(int position) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Sign out");
                builder.setMessage("Do you want sign out ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String,Object> tokenMapRemove=new HashMap<>();
                        tokenMapRemove.put("token_id", FieldValue.delete());
                        firebaseFirestore.collection("Users").document(user_id).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                finish();
                                firebaseAuth.signOut();
                                LoginManager.getInstance().logOut();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            }
        });
        infoRecyclerView.setAdapter(profileAdapter);
        recyclerViewlogout.setAdapter(logoutAdapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        RecyclerView.LayoutManager manager1 = new LinearLayoutManager(getApplicationContext());
        infoRecyclerView.setLayoutManager(manager);
        recyclerViewlogout.setLayoutManager(manager1);


        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), "Denied", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(ProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    } else {
                        BringImagePicker();
                    }
                } else {
                    BringImagePicker();
                }
            }
        });
        tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.dialog_edit_username, null);
                final EditText input = (EditText) view.findViewById(R.id.edit_username);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Edit Username");
                builder.setView(view);
                input.setText(tvUsername.getText());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = input.getText().toString();
                        tvUsername.setText(newName);
                        changge();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });
        tvStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.dialog_edit_username, null);
                final EditText input = (EditText) view.findViewById(R.id.edit_username);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Edit Status");
                builder.setView(view);
                input.setText(tvStatus.getText());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newStatus = input.getText().toString();
                        tvStatus.setText(newStatus);
                        changge();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();
            }
        });


    }

    private void changge() {
        final String username = tvUsername.getText().toString();
        final  String status =tvStatus.getText().toString();
        if (!username.equals("")  && !status.equals("") && mainImageURI != null) {
            progressBar.setVisibility(View.VISIBLE);
//nếu thay đổi
            if (isChanged) {
                user_id = firebaseAuth.getCurrentUser().getUid();
                StorageReference image_path = storageReference.child("profile_images").child(user_id + ".jpg");
                image_path.putFile(mainImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            storeFirebase(task, username,status);
                        } else {
                            Toast.makeText(getApplicationContext(), "Upload Error", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            } else {
                storeFirebase(null, username,status);
            }
        }
    }

    private void BringImagePicker() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(ProfileActivity.this);

    }


    private void storeFirebase(@NonNull Task<UploadTask.TaskSnapshot> task, String username,String status) {
        Uri download_uri;
        if (task != null) {
            download_uri = task.getResult().getDownloadUrl();
        } else {
            download_uri = mainImageURI;
        }
        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", username);
        userMap.put("status",status);
        userMap.put("image", String.valueOf(download_uri));
        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "The user are updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
        Toast.makeText(getApplicationContext(), "Đã Upload", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        changge();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                imgAvatar.setImageURI(mainImageURI);
                isChanged = true;
                changge();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void initView() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgAvatar = (CircleImageView) findViewById(R.id.img_avatar);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        infoRecyclerView = (RecyclerView) findViewById(R.id.info_recycler_view);
        recyclerViewlogout = (RecyclerView) findViewById(R.id.info_recycler_view_logout);
        tvStatus = (TextView) findViewById(R.id.tv_status);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
}
