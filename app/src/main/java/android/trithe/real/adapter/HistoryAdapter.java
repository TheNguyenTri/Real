package android.trithe.real.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.model.History;
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

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private final Context context;
    private List<History> list;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView planss;
        final TextView day;
        final ImageView avatar;
        final CheckBox checkBox;
        final RelativeLayout viewBackground;
        final RelativeLayout viewForeground;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tvNamepet);
            planss = view.findViewById(R.id.tvplanpet);
            day = view.findViewById(R.id.tvday);
            avatar = view.findViewById(R.id.avatar);
            checkBox = view.findViewById(R.id.cbo);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);
        }
    }


    public HistoryAdapter(Context mContext, List<History> albumList) {
        this.context = mContext;
        this.list = albumList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        History planss = list.get(position);
        holder.name.setText(planss.getIdpet());
        holder.planss.setText(planss.getName());
        holder.day.setText(planss.getTime() + " | " + sdf.format(planss.getDay()));
        switch (planss.getName()) {
            case "Đi bộ":
                Glide.with(context).load(R.drawable.dibo).into(holder.avatar);
                break;
            case "Cho ăn":
                Glide.with(context).load(R.drawable.doan).into(holder.avatar);
                break;
            case "Tắm rửa":
                Glide.with(context).load(R.drawable.tam).into(holder.avatar);
                break;
            case "Đi khám thú y":
                Glide.with(context).load(R.drawable.kham).into(holder.avatar);
                break;
            case "Mua thức ăn, phụ kiện":
                Glide.with(context).load(R.drawable.shop).into(holder.avatar);
                break;
            case "Khác":
                Glide.with(context).load(R.drawable.cander).into(holder.avatar);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


}
