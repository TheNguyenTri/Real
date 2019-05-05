package android.trithe.real.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.Logout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LogoutAdapter extends RecyclerView.Adapter<LogoutAdapter.MyViewHolder> {

    private final Context context;
    private List<Logout> list;
    private OnClick onClick;

    class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView icon;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_title);
            icon = view.findViewById(R.id.img_icon);
        }
    }


    public LogoutAdapter(Context mContext, List<Logout> albumList,OnClick onClick) {
        this.context = mContext;
        this.list = albumList;
        this.onClick=onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_logout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Logout planss = list.get(position);
        holder.name.setText(planss.getLabel());
        Glide.with(context).load(planss.getIcon()).into(holder.icon);
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClick.onItemClickClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
