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
import android.trithe.real.adapter.TintucAdapter;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.Tintuc;
import android.util.Log;
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
public class TintucFragment extends Fragment {
    private RecyclerView recyclerView;
    private TintucAdapter thucAnAdapter;
    private final List<Tintuc> list = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_thuc_don, container, false);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recycler_view);
        stringRequest();
        return view;
    }

    private void stringRequest() {
        thucAnAdapter = new TintucAdapter(getActivity(), list, new OnClick() {
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
        final String url = "https://moveek.com";
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String ten = "";
                String hinhanh = "";
                String gia = "";
                String url1 = "";
                org.jsoup.nodes.Document doc = Jsoup.parse(response);
                if (doc != null) {
                    Elements elements = doc.select("div.post");
                    for (org.jsoup.nodes.Element element : elements) {
                        org.jsoup.nodes.Element elementnho = element.select("div.post-thumbnail").first();
                        if (elementnho != null) {
                            ten = elementnho.getElementsByTag("a").attr("title");
                            url1 = elementnho.getElementsByTag("a").attr("href");
                            hinhanh = elementnho.getElementsByTag("img").attr("data-src");
                            gia = element.select("small.text-muted").text();
                            list.add(new Tintuc(ten, gia, hinhanh, url + url1));
                            thucAnAdapter.notifyDataSetChanged();
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
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(thucAnAdapter);
    }

}
