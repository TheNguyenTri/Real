package android.trithe.real.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.trithe.real.R;
import android.trithe.real.inter.OnClick;
import android.trithe.real.model.Lich;
import android.trithe.real.model.Tintuc;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class LichAdapter extends RecyclerView.Adapter<LichAdapter.MyViewHolder> {

    private final Context context;
    private final List<Lich> list;
    private final OnClick onClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView ngay;
        final TextView thu;

        MyViewHolder(View view) {
            super(view);
            ngay = view.findViewById(R.id.title);
            thu = view.findViewById(R.id.weight);
        }
    }

    //
//
    public LichAdapter(Context mContext, List<Lich> albumList, OnClick onClick) {
// --Commented out by Inspection STOP (06/11/2018 9:18 SA)
        this.context = mContext;
        this.list = albumList;
        this.onClick=onClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lich, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Lich browse = list.get(position);
        holder.ngay.setText(browse.getNgay());
        holder.thu.setText(browse.getThu());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
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