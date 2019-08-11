package android.trithe.real.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.trithe.real.R;
import android.trithe.real.model.BlogPost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SlidePaperAdapter extends PagerAdapter {
    private Context context;
    private List<BlogPost> slideList;

    public SlidePaperAdapter(Context context, List<BlogPost> slideList) {
        this.context = context;
        this.slideList = slideList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View slideLayout = inflater.inflate(R.layout.item_slide, null);
        ImageView slideImage = slideLayout.findViewById(R.id.img_flit);
        Glide.with(context).load(slideList.get(position).getImage_url()).into(slideImage);
        container.addView(slideLayout);
        return slideLayout;
    }

    @Override
    public int getCount() {
        return slideList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
