package android.trithe.real.fragment;


import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.activity.WebViewBrowseActivity;
import android.trithe.real.adapter.LichAdapter;
import android.trithe.real.adapter.LichChieuAdapter;
import android.trithe.real.adapter.TintucAdapter;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.Lich;
import android.trithe.real.model.LichChieu;
import android.trithe.real.model.Tintuc;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.trithe.real.R;
import android.widget.Toast;

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
public class LichChieuFragment extends Fragment {
    private RecyclerView recyclerView;
    private LichAdapter lichAdapter;
    private RecyclerView recyclerViewPhim;
    private final List<Lich> list = new ArrayList<>();
    private LichChieuAdapter lichChieuAdapter;
    private List<LichChieu> lichChieus = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_lich_chieu, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerViewPhim = (RecyclerView) view.findViewById(R.id.recycler_view_phim);
        stringRequest();
        stringRequest1();
        return view;
    }

    private void stringRequest() {
        lichAdapter = new LichAdapter(getActivity(), list, new OnClick() {
            @Override
            public void onItemClickClicked(int position) {

            }
        });
        list.clear();
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        final String url = "https://moveek.com/rap/lotte-melinh-plaza-ha-dong";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String ngay = "";
                String thu = "";
                String url1 = "";
                org.jsoup.nodes.Document doc = Jsoup.parse(response);
                if (doc != null) {
                    Elements elements = doc.select("div.col-xs-2");
                    for (org.jsoup.nodes.Element element : elements) {
                        if (element != null) {
                            ngay = element.select("a").select("span.h4").text();
                            thu = element.select("a").select("span.text-xs").text();
                            list.add(new Lich(ngay, thu, url + url1));
                            lichAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(lichAdapter);

    }

    private void stringRequest1() {
        lichChieuAdapter = new LichChieuAdapter(getActivity(), lichChieus, new OnClick() {
            @Override
            public void onItemClickClicked(int position) {
                Intent intent = new Intent(getActivity(), WebViewBrowseActivity.class);
                Bundle bundle = new Bundle();
                String a = lichChieus.get(position).getUrl();
                bundle.putString("url", a);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        lichChieus.clear();
        final RequestQueue requestQueue1 = Volley.newRequestQueue(getActivity());
        final String urls = "https://moveek.com/dang-chieu";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, urls, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                org.jsoup.nodes.Document doc = Jsoup.parse(response);
                if (doc != null) {
                    //all
                    Elements elements = doc.select("div.col-lg-2");
                    for (org.jsoup.nodes.Element element : elements) {
                        if (element != null) {
                            String names = element.select("div.panel").first().getElementsByTag("a").attr("title");
                            String images = element.select("div.panel").first().select("a").first().getElementsByTag("img").attr("data-src");
                            String url1 = element.select("div.panel").first().getElementsByTag("a").attr("href");
                            lichChieus.add(new LichChieu(names, images, "https://moveek.com"+url1));
                            lichChieuAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue1.add(stringRequest);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerViewPhim.setLayoutManager(mLayoutManager);
        recyclerViewPhim.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
        recyclerViewPhim.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPhim.setAdapter(lichChieuAdapter);

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
