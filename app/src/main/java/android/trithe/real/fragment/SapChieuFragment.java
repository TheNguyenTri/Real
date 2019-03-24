package android.trithe.real.fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.activity.WebViewBrowseActivity;
import android.trithe.real.adapter.BrowseAdapter;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.Browse;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.trithe.real.R;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SapChieuFragment extends Fragment {
    private RecyclerView recyclerView;
    private BrowseAdapter browseAdapter;
    private final List<Browse> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sap_chieu, container, false);  setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recycler_view);
        stringRequest();

        return view;
    }

    private void stringRequest(){
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

        list.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://moveek.com/sap-chieu/";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String ten = "";
                String hinhanh = "";
                String url = "";
                String timeline="";
                org.jsoup.nodes.Document doc = Jsoup.parse(response);
                if (doc != null) {
                    Elements elements = doc.select("div.col-lg-2");
                    for (org.jsoup.nodes.Element element : elements) {
                        org.jsoup.nodes.Element elementen = element.getElementsByTag("a").first();
                        org.jsoup.nodes.Element elemenimage = element.getElementsByTag("img").first();
                        org.jsoup.nodes.Element elemenurl = element.getElementsByTag("a").first();
                        org.jsoup.nodes.Element elementdate = element.select("div.panel-rating-box").first();
                        if (elementen != null) {
                            ten = elementen.attr("title");
                        }
                        if (elemenimage != null) {
                            hinhanh = elemenimage.attr("data-src");
                        }
                        if (elemenurl != null) {
                            url = elemenurl.attr("href");
                        }
                        if(elementdate != null){
                            timeline = elementdate.text();
                        }
                        list.add(new Browse(ten, "https://moveek.com"+url,timeline, hinhanh));
                        browseAdapter.notifyDataSetChanged();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(browseAdapter);
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
