package android.trithe.real.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.trithe.real.R;
import android.trithe.real.adapter.SlidePaperAdapter;
import android.trithe.real.fragment.NotificationFragment;
import android.trithe.real.fragment.PostFragment;
import android.trithe.real.fragment.ChatFragment;
import android.trithe.real.helper.BottomNavigationBehavior;
import android.trithe.real.model.Slide;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

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
    private EditText textNamePust;
    private List<Slide> slideList = new ArrayList<>();
    private ViewPager backdrop;
    private TabLayout indicator;
    private ImageView more;
    private BottomNavigationView bottomNavigationView;
    private DatabaseReference mUserRef;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFirebase();
        initView();
        setSupportActionBar(toolbar);
        getInfoCurrent();
        initCollapsingToolbar();
        textNamePust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), NewPostActivity.class));
            }
        });
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        bottomView();
        slide();
        gettimerSlide();
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initFirebase() {
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
        textNamePust = findViewById(R.id.textNamePust);
        backdrop = findViewById(R.id.backdrop);
        indicator = findViewById(R.id.indicator);
        planFragment = new ChatFragment();
        postFragment = new PostFragment();
        notificationFragment = new NotificationFragment();
        loadFragment(postFragment);
    }

    private void bottomView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
            }
        });
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });
    }

    private void gettimerSlide() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
        indicator.setupWithViewPager(backdrop, true);
        SlidePaperAdapter paperAdapter = new SlidePaperAdapter(this, slideList);
        backdrop.setAdapter(paperAdapter);
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
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String image = task.getResult().getString("image");
                        Glide.with(MainActivity.this).load(image).into(avatar);
                    } else {
                        Glide.with(MainActivity.this).load(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getPhotoUrl()).into(avatar);
                    }
                }
            }
        });
    }

    private void slide() {
        slideList.add(new Slide(R.drawable.alita));
        slideList.add(new Slide(R.drawable.alita2));
        slideList.add(new Slide(R.drawable.alita3));
        slideList.add(new Slide(R.drawable.infinity));
        slideList.add(new Slide(R.drawable.endgame));
    }


    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                findViewById(R.id.coll);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                    textNamePust.setVisibility(View.VISIBLE);

                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    textNamePust.setVisibility(View.INVISIBLE);
                    isShow = false;
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
    class SliderTimer extends TimerTask {
        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (backdrop.getCurrentItem() < slideList.size() - 1) {
                        backdrop.setCurrentItem(backdrop.getCurrentItem() + 1);
                    } else
                        backdrop.setCurrentItem(0);
                }
            });


        }
    }
}


