package android.trithe.real.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.trithe.real.R;
import android.trithe.real.fragment.NotificationFragment;
import android.trithe.real.fragment.PostFragment;
import android.trithe.real.fragment.ChatFragment;
import android.trithe.real.helper.BottomNavigationBehavior;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private ChatFragment planFragment;
    private NotificationFragment notificationFragment;
    private PostFragment postFragment;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private CircleImageView avatar;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;
    private EditText textNamePost;
    private ImageView more;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference mUserRef;
    private static final int REQUEST_CODE_SOME_FEATURES_PERMISSIONS = 5555;
    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFireBase();
        initView();
        setSupportActionBar(toolbar);
        getInfoCurrent();
        textNamePost.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), NewPostActivity.class));
        });
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        bottomView();
        more.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, AboutActivity.class)));
        if (!checkPermission(MainActivity.this, PERMISSIONS)) {
            requestPermission();
        }
    }

    private boolean checkPermission(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context != null && permissions != null) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
            return false;
        } else return true;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_CODE_SOME_FEATURES_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_SOME_FEATURES_PERMISSIONS) {
            for (int i = 0; i < PERMISSIONS.length; i++) {
                if (grantResults.length > 0 && grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermission();
                        }
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermission();
                        }
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermission();
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFireBase() {
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("TimeOnline").child(user_id);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbarmain);
        avatar = findViewById(R.id.avatar);
        more = findViewById(R.id.notifi);
        bottomNavigationView = findViewById(R.id.navi);
        textNamePost = findViewById(R.id.textNamePust);
        planFragment = new ChatFragment();
        postFragment = new PostFragment();
        notificationFragment = new NotificationFragment();
        loadFragment(postFragment);
    }

    private void bottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    loadFragment(postFragment);
                    return true;
                case R.id.browse:
                    loadFragment(notificationFragment);
                    return true;
                case R.id.plan:
                    loadFragment(planFragment);
                    return true;
            }
            return false;
        });
        avatar.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user_id != null) {
            firebaseFirestore.collection("Users").document(user_id).update("online", true);
            mUserRef.child("online").setValue(true);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (user_id != null) {
            firebaseFirestore.collection("Users").document(user_id).update("online", false);
            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    private void getInfoCurrent() {
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    String image = task.getResult().getString("image");
                    Glide.with(MainActivity.this).load(image).into(avatar);
                } else {
                    Glide.with(MainActivity.this).load(
                            Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhotoUrl()).into(avatar);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}


