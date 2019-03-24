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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private final Context context;

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


    public NotificationAdapter(Context mContext, List<Configuration> albumList) {
        this.context = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
    }

    @Override
    public int getItemCount() {
        return  0;
    }


}
