package android.trithe.real.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.WebViewBrowseActivity;
import android.trithe.real.adapter.BrowseAdapter;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.Browse;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class BrowseFragment extends Fragment {
    private RecyclerView recyclerView;
    private BrowseAdapter browseAdapter;
    private final List<Browse> list = new ArrayList<>();
    private TextView textView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.browse_fragment, container, false);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recycler_view);
        textView = view.findViewById(R.id.tvretry);

////

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
        Glide.with(getContext()).load(R.drawable.pet).into((ImageView) view.findViewById(R.id.backdrop));
        ////


        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://www.petmart.vn/shop";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String ten = "";
                String hinhanh = "";
                String url = "";
                org.jsoup.nodes.Document doc = Jsoup.parse(response);
                if (doc != null) {
                    Elements elements = doc.select("div.col-inner");
//                    Elements elements = doc.select("div.product-image-wrapper");
                    for (org.jsoup.nodes.Element element : elements) {
                        org.jsoup.nodes.Element elementen = element.getElementsByTag("p").first();
                        org.jsoup.nodes.Element elemenimage = element.getElementsByTag("img").first();
                        org.jsoup.nodes.Element elemenurl = element.getElementsByTag("a").first();
                        if (elementen != null) {
                            ten = elementen.text();
                        }
                        if (elemenimage != null) {
                            hinhanh = elemenimage.attr("src");
                        }
                        if (elemenurl != null) {
                            url = elemenurl.attr("href");
                        }
                        list.add(new Browse(ten, url, hinhanh));
                    }
                    browseAdapter = new BrowseAdapter(getActivity(), list, new OnClick() {
                        @Override
                        public void onItemClickClicked(int position) {
                            Intent intent = new Intent(getActivity(), WebViewBrowseActivity.class);
                            Bundle bundle = new Bundle();
                            String a = list.get(position).getUrl();
                            bundle.putString("url", a);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                    recyclerView.setAdapter(browseAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        ConstraintLayout constraintLayout = view.findViewById(R.id.ll1);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            requestQueue.add(stringRequest);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(browseAdapter);
        } else {
            constraintLayout.setVisibility(View.VISIBLE);
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                final ConstraintLayout constraintLayout = view.findViewById(R.id.ll1);
                if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
                    requestQueue.add(stringRequest);
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(browseAdapter);
                    constraintLayout.setVisibility(View.INVISIBLE);
                } else {
                    CoordinatorLayout coordinatorLayout = view.findViewById(R.id.cc);
                    constraintLayout.setVisibility(View.VISIBLE);
                    Snackbar.make(coordinatorLayout, "No network", Snackbar.LENGTH_LONG).show();
                }
            }
        });

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

}
