package android.trithe.real.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.database.HistoryDAO;
import android.trithe.real.database.PlanssDAO;
import android.trithe.real.inter.OnClick;
import android.trithe.real.inter.OnClick1;
import android.trithe.real.model.Configuration;
import android.trithe.real.model.History;
import android.trithe.real.model.Planss;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder> {

    private final Context context;
    private List<Configuration> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView email;
        final ImageView icon;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_title);
            email = view.findViewById(R.id.tv_detail);
            icon = view.findViewById(R.id.img_icon);
        }
    }


    public ProfileAdapter(Context mContext, List<Configuration> albumList) {
        this.context = mContext;
        this.list = albumList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_profile, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Configuration planss = list.get(position);
        holder.name.setText(planss.getLabel());
        holder.email.setText(planss.getValue());
        Glide.with(context).load(planss.getIcon()).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
