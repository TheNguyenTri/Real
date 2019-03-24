package android.trithe.real.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.activity.WebViewBrowseActivity;
import android.trithe.real.adapter.BrowseAdapter;
import android.trithe.real.adapter.PaperViewBrowseAdapter;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.Browse;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
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

public class BrowseFragment extends Fragment {
    private ViewPager viewPaper;
    private PaperViewBrowseAdapter mPaperViewBrowseAdapter;
    private TextView sapchieu;
    private TextView combodoan;
    private TextView lichchieu;
    private TextView textView;
    private LinearLayout tabLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.shop_fragment, container, false);
        setHasOptionsMenu(true);
        initView(view);
        checkPositionPaper();
        checkNetwork(view);
        return view;
    }

    private void checkPositionPaper(){
        viewPaper.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                changeTabs(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });


        sapchieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPaper.setCurrentItem(0);
            }
        });
        combodoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPaper.setCurrentItem(1);
            }
        });
        lichchieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPaper.setCurrentItem(2);
            }
        });
    }

    private void initView(View view){
        textView = view.findViewById(R.id.tvretry);
        viewPaper = (ViewPager) view.findViewById(R.id.view_paper);
        tabLayout = (LinearLayout) view.findViewById(R.id.tab_layout);
        mPaperViewBrowseAdapter = new PaperViewBrowseAdapter(getActivity().getSupportFragmentManager());
        viewPaper.setAdapter(mPaperViewBrowseAdapter);
        sapchieu = (TextView) view.findViewById(R.id.sapchieu);
        combodoan = (TextView) view.findViewById(R.id.combodoan);
        lichchieu = (TextView) view.findViewById(R.id.lichchieu);
    }

    private void checkNetwork(final View view) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        ConstraintLayout constraintLayout = view.findViewById(R.id.ll1);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
            tabLayout.setVisibility(View.VISIBLE);
            constraintLayout.setVisibility(View.INVISIBLE);
        } else {
            tabLayout.setVisibility(View.GONE);
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
                    tabLayout.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.INVISIBLE);
                } else {
                    CoordinatorLayout coordinatorLayout = view.findViewById(R.id.cc);
                    constraintLayout.setVisibility(View.VISIBLE);
                    tabLayout.setVisibility(View.INVISIBLE);
                    Snackbar.make(coordinatorLayout, "No network", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void changeTabs(int i) {
        if (i == 0) {
            combodoan.setTextColor(Color.RED);
            combodoan.setTextSize(18);

            lichchieu.setTextColor(Color.BLACK);
            lichchieu.setTextSize(15);

            sapchieu.setTextColor(Color.BLACK);
            sapchieu.setTextSize(15);
        } else if (i == 1) {
            sapchieu.setTextColor(Color.RED);
            sapchieu.setTextSize(18);

            combodoan.setTextColor(Color.BLACK);
            combodoan.setTextSize(15);

            lichchieu.setTextColor(Color.BLACK);
            lichchieu.setTextSize(15);
        } else if (i == 2) {
            lichchieu.setTextColor(Color.RED);
            lichchieu.setTextSize(18);

            combodoan.setTextColor(Color.BLACK);
            combodoan.setTextSize(15);

            sapchieu.setTextColor(Color.BLACK);
            sapchieu.setTextSize(15);
        }
    }


}
