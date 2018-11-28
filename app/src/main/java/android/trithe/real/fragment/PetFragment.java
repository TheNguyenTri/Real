package android.trithe.real.fragment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.trithe.real.R;
import android.trithe.real.activity.AboutActivity;
import android.trithe.real.activity.MainActivity;
import android.trithe.real.adapter.PetAdapter;
import android.trithe.real.database.PetDAO;
import android.trithe.real.database.TypeDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.inter.OnClick1;
import android.trithe.real.model.Pet;
import android.trithe.real.model.TypePet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class PetFragment extends Fragment {
    private TypeDAO typeDAO;
    private PetAdapter petAdapter;
    List<Pet> listpet = new ArrayList<>();
    private  ConstraintLayout constraintLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.pet_fragment, container, false);
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        Toolbar toolbar = view.findViewById(R.id.toolbar1);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ///
        ///
        //
        final CollapsingToolbarLayout collapsingToolbar = view.findViewById(R.id.coll);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
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
        ///
        //
        //
        final PetDAO petDAO = new PetDAO(getActivity());
        typeDAO = new TypeDAO(getActivity());

        constraintLayout = view.findViewById(R.id.ll);
        if (petDAO.getAllPet().size() == 0) {
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.GONE);
        }
        in();
        Glide.with(getContext()).load(R.drawable.pet).into((ImageView) view.findViewById(R.id.backdrop));
        listpet = petDAO.getAllPet();
        petAdapter = new PetAdapter(getActivity(), listpet, new OnClick() {
            @Override
            public void onItemClickClicked(int position) {
                getActivity().finish();

            }
        }, new OnClick1() {
            @Override
            public void onItemClickClicked(int position) {
                if (petDAO.getAllPet().size() == 0) {
                    constraintLayout.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    Log.e("abc", String.valueOf(petDAO.getAllPet().size()));
                } else {
                    constraintLayout.setVisibility(View.GONE);
                }

            }
        });
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(petAdapter);
        return view;
    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        GridSpacingItemDecoration(int spacing) {
            this.spanCount = 2;
            this.spacing = spacing;
            this.includeEdge = true;
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
//        getActivity().invalidateOptionsMenu();
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                petAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                petAdapter.getFilter().filter(query);
                return false;
            }
        });
    }

    //
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;

            case R.id.action_settings:
                startActivity(new Intent(getContext(), AboutActivity.class));
        }
        return super.onOptionsItemSelected(item);

    }

    private void in() {
        if (typeDAO.getAllType().size() == 0) {
            typeDAO.insertType(new TypePet("t1", "Chó"));
            typeDAO.insertType(new TypePet("t2", "Mèo"));
            typeDAO.insertType(new TypePet("t3", "Cá"));
            typeDAO.insertType(new TypePet("t4", "Chim"));
            typeDAO.insertType(new TypePet("t5", "Kagaroo"));
            typeDAO.insertType(new TypePet("t6", "Ếch"));
            typeDAO.insertType(new TypePet("t7", "Sóc"));
            typeDAO.insertType(new TypePet("t8", "Khác"));
        }
    }

}