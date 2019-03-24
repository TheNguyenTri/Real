package android.trithe.real.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.trithe.real.NewPostActivity;
import android.trithe.real.NotificationActivity;
import android.trithe.real.ProfileActivity;
import android.trithe.real.R;
import android.trithe.real.adapter.SlidePaperAdapter;
import android.trithe.real.fragment.BrowseFragment;
import android.trithe.real.fragment.PetFragment;
import android.trithe.real.fragment.PlanFragment;
import android.trithe.real.fragment.SapChieuFragment;
import android.trithe.real.fragment.StatisticsFragment;
import android.trithe.real.helper.BottomNavigationBehavior;
import android.trithe.real.model.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.theartofdev.edmodo.cropper.CropImage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity{
    private PlanFragment planFragment;
    private BrowseFragment browseFragment;
    private PetFragment petFragment;
    private StatisticsFragment statisticsFragment;
    //    private CircleImageView avatar;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private CircleImageView avatar;
    private String user_id;
    private FirebaseFirestore firebaseFirestore;
    private Uri mainImageURI = null;
    private EditText textNamePust;
    private List<Slide> slideList = new ArrayList<>();
    private ViewPager backdrop;
    private SlidePaperAdapter paperAdapter;
    private TabLayout indicator;
    private ImageView notifi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbarmain);
        avatar = (CircleImageView) findViewById(R.id.avatar);
        notifi = (ImageView) findViewById(R.id.notifi);

        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navi);
        statisticsFragment = new StatisticsFragment();
        initCollapsingToolbar();
        planFragment = new PlanFragment();
        petFragment = new PetFragment();
        browseFragment = new BrowseFragment();
        loadFragment(petFragment);
        textNamePust = (EditText) findViewById(R.id.textNamePust);
        textNamePust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), NewPostActivity.class));
            }
        });
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String image = task.getResult().getString("image");
                        mainImageURI = Uri.parse(image);
                        Glide.with(MainActivity.this).load(image).into(avatar);
                    } else {
                        Glide.with(MainActivity.this).load(firebaseAuth.getCurrentUser().getPhotoUrl()).into(avatar);
                    }
                }
            }
        });

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            }
        });


        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        loadFragment(petFragment);
                        textNamePust.setText("What are you thinking ?");
                        textNamePust.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                                startActivity(new Intent(getApplicationContext(), NewPostActivity.class));
                            }
                        });
                        return true;
                    case R.id.browse:
                        loadFragment(browseFragment);
                        return true;
                    case R.id.setting:
                        loadFragment(statisticsFragment);
                        textNamePust.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                                startActivity(new Intent(getApplicationContext(),NewTodoActivity.class));
                            }
                        });
                        return true;
                    case R.id.plan:
                        loadFragment(planFragment);
                        return true;
                }
                return false;
            }
        });
        backdrop = (ViewPager) findViewById(R.id.backdrop);
        indicator = (TabLayout) findViewById(R.id.indicator);
        slide();
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(),4000,6000);
        indicator.setupWithViewPager(backdrop,true);
        paperAdapter = new SlidePaperAdapter(this,slideList);
        backdrop.setAdapter(paperAdapter);

        notifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
            }
        });

    }
    private void slide(){
        slideList.add(new Slide(R.drawable.alita));
        slideList.add(new Slide(R.drawable.alita2));
        slideList.add(new Slide(R.drawable.alita3));
        slideList.add(new Slide(R.drawable.infinity));
        slideList.add(new Slide(R.drawable.endgame));
    }


    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.coll);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
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
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frameLayout, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main2,menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        startActivity(new Intent(getApplicationContext(), AboutActivity.class));
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageURI = result.getUri();
                avatar.setImageURI(mainImageURI);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    class SliderTimer extends TimerTask {


        @Override
        public void run() {

            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (backdrop.getCurrentItem()<slideList.size()-1) {
                        backdrop.setCurrentItem(backdrop.getCurrentItem()+1);
                    }
                    else
                        backdrop.setCurrentItem(0);
                }
            });


        }
    }
}


