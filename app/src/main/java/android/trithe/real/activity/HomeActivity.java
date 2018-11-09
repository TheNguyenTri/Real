package android.trithe.real.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.trithe.real.R;
import android.trithe.real.adapter.PetAdapter;
import android.trithe.real.database.PetDAO;
import android.trithe.real.database.TypeDAO;
import android.trithe.real.model.Pet;
import android.trithe.real.model.TypePet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private TypeDAO typeDAO;
    private PetDAO petDAO;
    private RecyclerView recyclerView;
    private PetAdapter petAdapter;
    private List<Pet> listpet;
    private List<TypePet> typePetList =new ArrayList<>();
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;

    @SuppressLint({"RestrictedApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        setSupportActionBar(toolbar);
        initCollapsingToolbar();
        petDAO = new PetDAO(getApplicationContext());
        typeDAO = new TypeDAO(getApplicationContext());
        listpet = new ArrayList<>();
        in();
        listpet = petDAO.getAllPet();
        petAdapter = new PetAdapter(this, listpet);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(petAdapter);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.browse:
                    startActivity(new Intent(getApplicationContext(), BrowseActivity.class));
                    break;
                case R.id.setting:
                    startActivity(new Intent(getApplicationContext(), StatisticsActivity.class));
                    break;
                case R.id.plan:
                    startActivity(new Intent(getApplicationContext(), PlanActivity.class));
                    break;
            }
            return false;
        }
    };


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
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.navi);
    }


    @Override
    protected void onResume() {
        super.onResume();
        listpet.clear();
        listpet = petDAO.getAllPet();
        petAdapter.changeDataset(listpet);
    }

    public void addpet(View view) {
        startActivity(new Intent(getApplicationContext(), PetActivity.class));
    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);

    }

    public void in() {
        if (typeDAO.getnullType() ==0) {
            typeDAO.insertType(new TypePet("t1", "Chó", R.drawable.dog));
            typeDAO.insertType(new TypePet("t2", "Mèo", R.drawable.cat));
            typeDAO.insertType(new TypePet("t3", "Cá", R.drawable.ca));
            typeDAO.insertType(new TypePet("t4", "Chim", R.drawable.chim));
            typeDAO.insertType(new TypePet("t5", "Kagaroo", R.drawable.kagaroo));
            typeDAO.insertType(new TypePet("t6", "Ếch", R.drawable.ech));
            typeDAO.insertType(new TypePet("t7", "Sóc", R.drawable.soc));
        }
    }
}
//             bundle.putByteArray("IMAGE", list.get(position).getImage());
//                    intent.putExtras(bundle);