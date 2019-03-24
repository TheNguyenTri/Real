package android.trithe.real.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.Lich;
import android.trithe.real.model.LichChieu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LichChieuAdapter extends RecyclerView.Adapter<LichChieuAdapter.MyViewHolder> {

    private final Context context;
    private final List<LichChieu> list;
    private final OnClick onClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView name;
        final ImageView imageView;

        MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.title);
            imageView=view.findViewById(R.id.thumbnail);
        }
    }

    //
//
    public LichChieuAdapter(Context mContext, List<LichChieu> albumList, OnClick onClick) {
// --Commented out by Inspection STOP (06/11/2018 9:18 SA)
        this.context = mContext;
        this.list = albumList;
        this.onClick=onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lich_chieu, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final LichChieu browse = list.get(position);
        holder.name.setText(browse.getName());
        Glide.with(context).load(browse.getImage()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
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