package android.trithe.real.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.trithe.real.R;
import android.trithe.real.fragment.BrowseFragment;
import android.trithe.real.fragment.PetFragment;
import android.trithe.real.fragment.PlanFragment;
import android.trithe.real.fragment.StatisticsFragment;
import android.trithe.real.helper.BottomNavigationBehavior;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private PlanFragment planFragment;
    private BrowseFragment browseFragment;
    private PetFragment petFragment;
    private StatisticsFragment statisticsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.navi);
        floatingActionButton = findViewById(R.id.fabpet);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(MainActivity.this, PetsActivity.class));
            }
        });
        statisticsFragment = new StatisticsFragment();
//        initCollapsingToolbar();
        planFragment = new PlanFragment();
        petFragment = new PetFragment();
        browseFragment = new BrowseFragment();
        loadFragment(petFragment);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        floatingActionButton.setVisibility(View.VISIBLE);
                        loadFragment(petFragment);
                        return true;
                    case R.id.browse:
                        loadFragment(browseFragment);
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.setting:
                        loadFragment(statisticsFragment);
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        return true;
                    case R.id.plan:
                        loadFragment(planFragment);
                        floatingActionButton.setVisibility(View.INVISIBLE);
                        return true;
                }
                return false;
            }
        });
//        Glide.with(this).load(R.drawable.dogcat).into((ImageView) findViewById(R.id.backdrop));
    }


//    private void initCollapsingToolbar() {
//        final CollapsingToolbarLayout collapsingToolbar =
//                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(" ");
//        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
//        appBarLayout.setExpanded(true);
//
//        // hiding & showing the title when toolbar expanded & collapsed
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle(getString(R.string.app_name));
//                    isShow = true;
//                } else if (isShow) {
//                    collapsingToolbar.setTitle(" ");
//                    isShow = false;
//                }
//            }
//        });
//    }


//    private void showFragment(Fragment fragment){
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.frameLayout, fragment);
//        ft.commit();
//    }

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
}
